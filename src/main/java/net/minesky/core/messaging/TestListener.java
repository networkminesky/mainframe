package net.minesky.core.messaging;

import net.minesky.core.CoreMain;
import net.minesky.core.speedtest.SpeedTest;

public class TestListener implements MessagingListener {

    @Override
    public void onDocumentUpdated(String channel, String value) {

        //CoreMain.logger.info("[Messaging-Debug] Detected messaging: "+channel+ " - value: "+value + " - envinronment: "+CoreMain.currentPlatform);

        if(channel.equals("test-speed-results")) {
            if(CoreMain.currentPlatform == CoreMain.PlatformType.VELOCITY) {
                for(String d : value.split("☺")) {
                    CoreMain.logger.info(d);
                }
            }
        }

        if(channel.equals("test-speed")) {
            if(CoreMain.currentPlatform == CoreMain.PlatformType.SPIGOT)
                SpeedTest.detectMongoSpeed(Long.parseLong(value));
        }

    }

}
