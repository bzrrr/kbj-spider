package com.bzrrr.kbjspider.domain;

/**
 * @Author: wangziheng
 * @Date: 2021/3/18
 */
public enum InsCookie {
    MAIN_COOKIE(1,"ig_did=76CC92A7-2222-4163-95A6-8408B45877CC; mid=X4HTdQALAAHIbGcR8fnk0G-YI5fr; ig_nrcb=1; shbid=19826; rur=ATN; csrftoken=artGKMFTdR1wT4oOfWO96YoiWLayjfq7; ds_user_id=6031262926; sessionid=6031262926%3A9qNYXSXl5755pu%3A25; shbts=1616055727.1496713"),
    COOKIE1(2,"ig_did=76CC92A7-2222-4163-95A6-8408B45877CC; mid=X4HTdQALAAHIbGcR8fnk0G-YI5fr; ig_nrcb=1; shbid=19826; shbts=1616053745.435609; rur=ATN; csrftoken=DDz6yaupmrSFB21qJecgJBfx0kLzUqGw; ds_user_id=46662555736; sessionid=46662555736%3AkykTkqrb9I7quk%3A7");

    private int id;
    private String cookie;

    InsCookie(int id, String cookie) {
        this.id=id;
        this.cookie=cookie;
    }

    public String getCookie() {
        return cookie;
    }
}
