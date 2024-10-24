package com.trading.backend.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author ~~ trading.s
 * @date 12:02 10/18/21
 */
@Getter @Setter
@Component
@ConfigurationProperties(prefix = "remote-call")
public class RemoteServerProperty {


    private String domain = "https://internal-gateway-dev.tradingainsider.com";

    private EarnServer earnServer;

    private CrexServer crexServer;

    private AssetServer assetServer;

    private UserServer userServer;

    private CommonConfig commonConfig;


    @Getter @Setter
    public static class EarnServer {
        private String positionList;
    }

    @Getter @Setter
    public static class CrexServer {
        private String price;
        private String indexPrice;
    }

    @Getter @Setter
    public static class AssetServer {
        private String activityDeposit;
    }

    @Getter @Setter
    public static class UserServer {
        private String kycStatus;
        private String profileBatch;
        // https://apisix-gateway-sit.tradingainsider.com/user-center/doc.html#/default/%E9%80%9A%E7%94%A8%E8%A7%A6%E8%BE%BE%E5%8F%91%E9%80%81%E6%8E%A5%E5%8F%A3(%E5%AF%B9%E5%86%85)/syncInternalSendCommonMessageUsingPOST
        private String messageInform;
        private String userList;
        private String batchKycStatus;
    }

    @Getter @Setter
    public static class CommonConfig {
        private String dividedCoin;
    }
}
