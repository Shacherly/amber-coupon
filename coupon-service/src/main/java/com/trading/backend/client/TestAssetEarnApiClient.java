package com.trading.backend.client;


import com.trading.backend.asset.client.feign.AssetEarnApi;
import org.springframework.cloud.openfeign.FeignClient;


/**
 * @author ~~ trading.s
 * @date 20:57 09/24/21
 * 假如我要调用 AssetEarnApi 的服务，不是直接用吗，服务
 */
@Deprecated
@FeignClient(name = "asset-service", contextId = "asset")
public interface TestAssetEarnApiClient /*extends AssetEarnApi*/ {

}
