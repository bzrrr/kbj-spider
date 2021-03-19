package com.bzrrr.kbjspider.domain;

/**
 * @Author: wangziheng
 * @Date: 2021/3/18
 */
public enum InsCookie {
    MAIN_COOKIE(1,"ig_did=76CC92A7-2222-4163-95A6-8408B45877CC; mid=X4HTdQALAAHIbGcR8fnk0G-YI5fr; ig_nrcb=1; shbid=19826; shbts=1616055727.1496713; rur=ATN; ds_user_id=6031262926; sessionid=6031262926%3ARtBKWUMNgrhDOU%3A2; csrftoken=9X1G1a2tcNcrPHJFDCXvw1IiOVlZeWA5"),
    BZRRR001(2,"ig_did=76CC92A7-2222-4163-95A6-8408B45877CC; mid=X4HTdQALAAHIbGcR8fnk0G-YI5fr; ig_nrcb=1; shbid=19826; shbts=1616053745.435609; rur=ATN; csrftoken=DDz6yaupmrSFB21qJecgJBfx0kLzUqGw; ds_user_id=46662555736; sessionid=46662555736%3AkykTkqrb9I7quk%3A7"),
    BZRRR002(3,"ig_did=42F882AD-1FA7-4D96-8A0B-2BCC07B0A44B; mid=YFP4TQALAAG_gcFbnJeJY99K9XDz; ig_nrcb=1; ds_user_id=46661524670; sessionid=46661524670%3A2PpbOciDqHa4aC%3A21; csrftoken=TV8pHGPgPL2gEnKQVik0ofWrNlsan0fs; rur=RVA");

    private int id;
    private String cookie;

    InsCookie(int id, String cookie) {
        this.id=id;
        this.cookie=cookie;
    }

    public String getCookie() {
        return cookie;
    }

    public static InsCookie getInstance(String cookie) {
        for (InsCookie insCookie : InsCookie.values()) {
            if (insCookie.getCookie().equals(cookie)) {
                return insCookie;
            }
        }
        return null;
    }
}
