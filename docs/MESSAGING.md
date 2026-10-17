# Mainframe — Messaging entre servidores

O **Mainframe Messaging** é o sistema de comunicação entre proxy (Velocity), backends (Folia/Paper) e qualquer JVM que rode o `mainframe-api`. Ele usa **MongoDB Change Streams** na collection `minesky.messaging` em vez de plugin channels (BungeeCord / Velocity).

---

## Como funciona (visão técnica)

```
┌─────────────┐     insert (documento)      ┌──────────────────┐
│  Servidor A │ ──────────────────────────► │ minesky.messaging │
└─────────────┘                             └────────┬─────────┘
                                                       │ Change Stream (insert)
                                                       ▼
                                              ┌──────────────────┐
                                              │ MessagingObserver │
                                              │  (thread dedicada)│
                                              └────────┬─────────┘
                                                       │ notifyListeners()
         ┌─────────────────────────────────────────────┼─────────────────────────────────────────────┐
         ▼                                             ▼                                             ▼
   Servidor A                                     Servidor B                                     Servidor C
   (listener local)                               (listener local)                               (listener local)
```

1. O emissor monta um **envelope** (`NetworkMessage`) com `channel`, `subchannel`, tipo de payload, origem e timestamp.
2. O documento é **inserido** na collection `messaging` do database `minesky`.
3. Todos os servidores com Mainframe ativo e messaging habilitado **observam** inserts via Change Stream.
4. Cada servidor entrega a mensagem aos **listeners** registrados cujo `channel` coincide (ou que optaram por escutar outros canais).
5. O documento é **removido** após o processamento para não acumular lixo na collection.

O messaging só inicia se **não** existir o arquivo `disable-messaging.txt` na pasta de dados do plugin e se a conexão MongoDB estiver configurada (`credentials.txt`).

---

## Conceitos

| Campo | Descrição |
|--------|-----------|
| **channel** | Namespace lógico (ex.: `mainframe:default`, `meuplugin:economy`). Evita colisão entre plugins. |
| **subchannel** | Ação ou tópico dentro do channel (ex.: `sync-balance`, `reload-config`). |
| **MessageType** | Tipo do payload: `STRING`, `INTEGER`, `LONG`, `DOUBLE`, `BOOLEAN`, `DOCUMENT`, `JSON`. |
| **source** | UUID do servidor emissor (preenchido automaticamente ao publicar). |
| **headers** | Metadados opcionais (pares chave/valor) para correlação, versão de protocolo, etc. |

Canal padrão quando você usa o atalho `Messaging.publish(subchannel, valor)`: **`mainframe:default`**.

---

## Dependência

No `pom.xml` do seu plugin:

```xml
<dependency>
    <groupId>net.minesky</groupId>
    <artifactId>mainframe-api</artifactId>
    <version>2.0.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

No servidor, o **Mainframe** precisa estar instalado e conectado ao mesmo MongoDB que os outros nós da rede.

---

## Registrar um listener

```java
import net.minesky.api.messaging.Messaging;
import net.minesky.core.messaging.MessageType;
import net.minesky.core.messaging.MessagingListener;
import net.minesky.core.messaging.NetworkMessage;
import org.bson.Document;

public class MeuPlugin extends JavaPlugin {

    private final MessagingListener economyListener = new MessagingListener("meuplugin:economy") {
        @Override
        public void onMessage(String subchannel, String value) {
            // Compatível com plugins antigos — só strings
            if ("ping".equals(subchannel)) {
                getLogger().info("Ping: " + value);
            }
        }

        @Override
        public void onMessage(NetworkMessage message) {
            if (!"sync".equals(message.subchannel())) {
                return;
            }
            switch (message.type()) {
                case LONG -> aplicarSaldo(message.asLong());
                case DOCUMENT -> aplicarSaldo(message.asDocument());
                case STRING -> getLogger().info(message.asString());
                default -> { }
            }
        }
    };

    @Override
    public void onEnable() {
        if (Messaging.hasObserver()) {
            Messaging.registerListener("MeuPlugin", economyListener);
        } else {
            getLogger().warning("Mainframe messaging desabilitado neste servidor.");
        }
    }

    @Override
    public void onDisable() {
        Messaging.unregisterListener(economyListener);
    }

    private void aplicarSaldo(long coins) { /* ... */ }
    private void aplicarSaldo(Document doc) { /* ... */ }
}
```

Para escutar **vários channels** no mesmo listener:

```java
new MessagingListener("mainframe:default", true) { /* listenToOtherChannels = true */ }
```

---

## Publicar mensagens

### String (atalho)

```java
Messaging.publish("economy:notify", "Servidor lobby reiniciou");
// channel implícito: mainframe:default — prefira o formato completo abaixo em plugins novos
```

### Canal + subchannel + tipo

```java
import net.minesky.core.messaging.MessageType;

Messaging.publish("meuplugin:economy", "balance-update", MessageType.LONG, 1500L);

Messaging.publish(
        "meuplugin:economy",
        "player-snapshot",
        MessageType.DOCUMENT,
        new Document("uuid", uuid).append("coins", 1500)
);

Messaging.publish(
        "meuplugin:config",
        "reload",
        MessageType.JSON,
        "{\"module\":\"shops\",\"version\":3}"
);
```

### Envelope completo (headers, timestamp customizado)

```java
import net.minesky.core.messaging.MessagingSender;
import net.minesky.core.messaging.NetworkMessage;
import net.minesky.core.messaging.MessageType;

import java.util.Map;

NetworkMessage msg = new NetworkMessage(
        "meuplugin:admin",
        "broadcast-console",
        MessageType.STRING,
        "Manutenção em 5 minutos",
        null, // source preenchido no publish
        System.currentTimeMillis(),
        Map.of("priority", "high", "locale", "pt-BR")
);
MessagingSender.publish(msg);
```

---

## Testar no jogo

Com permissão `mainframe.admin`:

```
/mainframe debug-messaging
```

Envia uma mensagem de teste no channel `mainframe:default` / subchannel `testchannel`.

Comparar latência com plugin messaging (apenas no Velocity, com backend configurado):

```
/mainframe testspeed <nome-do-servidor-backend>
```

Dispara o mesmo evento via **Mongo** e via **plugin channel**; os resultados aparecem no console (o teste de plugin channel ainda exige player online no backend — ver seção abaixo).

---

## Vantagens em relação a Plugin Messaging (BungeeCord / Velocity)

| | **Mainframe Messaging (MongoDB)** | **Plugin channels (Bungee/Velocity)** |
|---|-----------------------------------|----------------------------------------|
| **Player online** | Não precisa. Servidores vazios recebem e enviam normalmente. | Na prática, muitos fluxos exigem um jogador conectado como “portador” do pacote (ex.: `ConnectOther`, forwards via player). |
| **Proxy obrigatório** | Não. Dois backends podem conversar direto pela rede Mongo. | Mensagens costumam passar pelo proxy ou pelo caminho player → proxy → backend. |
| **Tipos de dados** | `STRING`, números, `Document`, JSON estruturado, headers. | Payload binário limitado; você serializa tudo manualmente (UTF, bytes). |
| **Descoberta / fan-out** | Todos os nós inscritos no Change Stream recebem o mesmo evento. | Um envio por conexão/player; broadcast exige lógica extra no proxy. |
| **Latência típica** | Baixa para rede MineSky (dezenas de ms, depende do MongoDB). Em testes internos (`testspeed`), costuma ser **mais rápido** que plugin messaging. | Mais hops (jogo + proxy + backend), serialização e filas do protocolo Minecraft. |
| **Operação** | Requer MongoDB estável e Change Streams habilitados (replica set / Atlas). | Só precisa do proxy e plugins; sem banco extra. |

**Quando ainda usar plugin messaging:** pacotes ligados ao **cliente** (mods, resource packs, ações que só fazem sentido com jogador na conexão). Para **sincronização de rede**, economia global, comandos de console remotos, discovery de servidores e eventos admin, o messaging do Mainframe é o caminho recomendado.

---

## Boas práticas

1. **Prefixe o channel** com o id do plugin (`meuplugin:...`) para não colidir com `mainframe:default` ou outros plugins MineSky.
2. **Use subchannels estáveis** como contrato de API (`economy:sync-v2`), não strings livres sem documentação.
3. **Prefira `MessageType.DOCUMENT` ou `JSON`** para payloads complexos em vez de concatenar strings.
4. **Mantenha listeners leves** — trabalho pesado (IO, DB) deve ir para `MainframeRuntime.async()` ou scheduler da plataforma.
5. **Não desabilite messaging** em produção sem motivo (`disable-messaging.txt`).

---

## Desabilitar messaging

Crie um arquivo vazio na pasta de dados do Mainframe:

```
plugins/mainframe/disable-messaging.txt
```

(Reinicie o servidor.) `Messaging.hasObserver()` retornará `false` e `registerListener` lançará exceção se chamado sem checagem.

---

## API pública (resumo)

| Classe | Uso |
|--------|-----|
| `net.minesky.api.messaging.Messaging` | `registerListener`, `publish`, `hasObserver` |
| `net.minesky.core.messaging.MessagingListener` | Base para listeners (string + `NetworkMessage`) |
| `net.minesky.core.messaging.NetworkMessage` | Envelope recebido nos listeners |
| `net.minesky.core.messaging.MessageType` | Tipos de payload |
| `net.minesky.core.messaging.MessagingSender` | Publicação avançada (`publish(NetworkMessage)`) |

Métodos legados `MessagingSender.sendMessage(...)` existem mas estão **deprecated** — use `publish`.

---

## Requisitos MongoDB

- Mesma URI em todos os servidores (`credentials.txt`).
- Change Streams suportados pelo deployment (MongoDB Atlas ou replica set; instância standalone antiga pode não suportar `watch`).

Se o observer falhar, o Mainframe **reconecta** o Change Stream após uma pausa curta; erros são logados em `[Messaging]`.
