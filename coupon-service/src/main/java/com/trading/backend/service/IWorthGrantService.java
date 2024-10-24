package com.trading.backend.service;

import com.trading.backend.bo.WorthGrantBO;
import com.trading.backend.bo.WorthToGrantBO;
import com.trading.backend.pojo.PossessDO;

import java.util.List;
import java.util.function.Consumer;

public interface IWorthGrantService {

    List<WorthToGrantBO> scanWorthGrants();

    void llegalInspect(WorthGrantBO grantBo, Consumer<String> consumer);

    void dailyCellingInspect(WorthGrantBO grantBo);

    void positionInspect(PossessDO possessDo);

    void doCashGrant(WorthGrantBO grantBo, PossessDO possessDo);

}
