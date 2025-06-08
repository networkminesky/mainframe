package net.minesky.core.messaging;

import net.minesky.core.CoreMain;
import net.minesky.core.speedtest.SpeedTest;

public class DefaultListener extends MessagingListener {

    public DefaultListener() {
        super("mainframe:default");
    }

    @Override
    public void onMessage(final String subchannel, final String value) {

        //CoreMain.logger.info("[Messaging-Debug] Detected messaging: "+channel+ " - value: "+value + " - envinronment: "+CoreMain.currentPlatform);

        switch(subchannel) {
            case "test-speed-results": {
                if(CoreMain.isOnVelocity()) {
                    // not the best method, needs future changes
                    for(String d : value.split("☺")) {
                        CoreMain.logger.info(d);
                    }
                }
                break;
            }

            case "discovery-result": {
                

                break;
            }

            case "server-discovery": {
                MessagingSender.sendMessage("discovery-result", "Me");
                break;
            }

            case "control": {

                if (value.equals("restart")) {



                }

            }

            case "test-speed": {
                if(!CoreMain.isOnVelocity())
                    SpeedTest.detectMongoSpeed(Long.parseLong(value));
                break;
            }
        }
    }
}
