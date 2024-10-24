package com.trading.backend.client;

import io.renren.commons.tools.page.PageData;

public interface IUserTagServiceApi {

    PageData<String> getTagUidPage(int page, int pageSize, String tagCode);

    PageData<String> getTagsUidPage(int page, int pageSize, String tags);

    long getTagTotal(String tagCode);

    long getTagsTotal(String tags);
}
