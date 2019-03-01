package com.mb.android.maiboapp.constants;

/**
 * 常量配置
 *
 * @author cgy
 */
public class ProjectConstants {


    /**
     * Bundle参数（k-v）
     */
    public class BundleExtra {
    	public static final String KEY_WEB_DETAIL_URL = "KEY_WEB_DETAIL_URL";
    	public static final String KEY_WEB_DETAIL_TITLE = "KEY_WEB_DETAIL_TITLE";
    	public static final String KEY_IS_SET_TITLE = "KEY_IS_SET_TITLE";
    	public static final String KEY_IS_CLOSE = "KEY_IS_CLOSE";
    	 /**
         * 手机号
         */
        public static final String KEY_USER_PHONE = "KEY_USER_PHONE";
        /**
         * 密码
         */
        public static final String KEY_USER_PWD = "KEY_USER_PWD";
        /**
         * 用户id
         */
        public static final String KEY_USER_ID = "KEY_USER_ID";
        /**
         * 用户名
         */
        public static final String KEY_USER_NAME = "KEY_USER_NAME";
        /**
         * 分组列表
         */
        public static final String KEY_GROUP = "KEY_GROUP";
        /**
         * 最新脉搏id
         */
        public static final String KEY_LAST_MB_ID = "KEY_LAST_MB_ID";
    }

    /**
     * Activity 请求码
     *
     * @author cgy
     */
    public class ActivityRequestCode {
        public static final int REQUEST_HOME_LOCATION = 001;
    }

    /**
     * Activity 返回码
     *
     * @author cgy
     */
    public class ActivityResultCode {

    }

    public static class Url {
        /**
         * APP的接口地址
         */
        public static final String INDEX_URL = "http://api.yuyintu.cn";
        /**
         * 首页脉搏列表
         */
        public static final String HOME_MBS = INDEX_URL + "/Home/index";
        /**
         * 广告
         */
        public static final String COMMON_ADS = INDEX_URL + "/Public/advs";
        /**
         * 好友圈脉搏列表
         */
        public static final String FRIENDS_MBS = INDEX_URL + "/Home/lstAirings";
        /**
         * 发脉搏
         */
        public static final String POST_MB = INDEX_URL + "/Home/add";
        /**
         * 发长脉搏
         */
        public static final String POST_MB_LONG = INDEX_URL + "/Article/add";
        /**
         * 收藏
         */
        public static final String COLLECT_MB = INDEX_URL + "/Collect/config";
        /**
         * 转发脉搏
         */
        public static final String REPOST_MB = INDEX_URL + "/Home/replay";
        /**
         * 评论脉搏
         */
        public static final String COMMENT_MB = INDEX_URL + "/Comment/add";
        /**
         * 点赞脉搏
         */
        public static final String GOOD_MB = INDEX_URL + "/Praise/add";
        /**
         * 删除脉搏
         */
        public static final String DELETE_MB = INDEX_URL + "/Public/del";
        /**
         * 关注
         */
        public static final String FOLLOWS_ADD = INDEX_URL + "/Follow/add";
        /**
         * 取消关注
         */
        public static final String FOLLOWS_DEL = INDEX_URL + "/Follow/del";
        /**
         * 删除评论
         */
        public static final String COMMENT_DEL = INDEX_URL + "/Comment/del";
        /**
         * 我的脉搏列表
         */
        public static final String ACCOUNT_MB = INDEX_URL + "/Airing/getAiringByUid";
        /**
         * 我的赞
         */
        public static final String ACCOUNT_GOOD = INDEX_URL + "/praise/index";
        /**
         * 我的评论
         */
        public static final String ACCOUNT_COMMENT = INDEX_URL + "comment_list ";
        /**
         * 赞我的
         */
        public static final String MESSAGE_GOOD = INDEX_URL + "/Notice/praise_list";
        /**
         * 评论我的
         */
        public static final String MESSAGE_COMMENT = INDEX_URL + "/Notice/comment_list";
        /**
         * @我的
         */
        public static final String MESSAGE_AT = INDEX_URL + "/Notice/at_list";
        /**
         * 脉搏详情
         */
        public static final String MB_DETAIL = INDEX_URL + "/Airing/getAiringById";
        /**
         * 脉搏转发列表
         */
        public static final String MB_REPOSTS = INDEX_URL + "/Airing/lstzfAirings";
        /**
         * 脉搏评论列表
         */
        public static final String MB_COMMENTS = INDEX_URL + "/Comment/lstCommentsByAid";
        /**
         * 脉搏点赞用户列表
         */
        public static final String MB_GOODS_LIST = INDEX_URL + "/praise/lst";
  
        
        /**
         * 读取用户资料
         */
        public static final String SHOW_USER_INFO = INDEX_URL + "/member/getMemberInfo";
        /**
         * 读取用户资料
         */
        public static final String SHOW_USER_INFO1 = INDEX_URL + "/member/getMemberInfoByUserName";
        /**
         * 创建用户
         */
        public static final String ACCOUNT_CREATE = INDEX_URL + "/register/index";
        /**
         * 用户是否存在
         */
        public static final String ACCOUNT_EXIT = INDEX_URL + "/Register/checkUserNameExit";
        /**
         * 手机是否存在
         */
        public static final String ACCOUNT_EXIT_PHONE = INDEX_URL + "/register/checkPhoneExit";
        /**
         * 登陆
         */
        public static final String ACCOUNT_LOGIN = INDEX_URL + "/login/index";
        /**
         * 获取手机验证码
         */
        public static final String ACCOUNT_CODE = INDEX_URL + "/register/sendPhoneTicket";
        /**
         * 获取手机验证码
         */
        public static final String ACCOUNT_CODE1 = INDEX_URL + "/Public/sendPhoneTicket";
        /**
         * 注销
         */
        public static final String ACCOUNT_LOGOUNT = INDEX_URL + "/Logout/index";
        /**
         * 设置密码
         */
        public static final String ACCOUNT_SETPASSWORD = INDEX_URL + "/Member/updatePassword";
        /**
         * 设置手机号
         */
        public static final String ACCOUNT_SETPHONE = INDEX_URL + "/Member/bindPhone";
        /**
         * 设置用户名
         */
        public static final String ACCOUNT_SETNAME = INDEX_URL + "/Member/modifyUsername";
        /**
         * 设置简介
         */
        public static final String ACCOUNT_SETINTRO = INDEX_URL + "/Member/modifyIntro";
        /**
         * 修改用户信息
         */
        public static final String ACCOUNT_UPDATE = INDEX_URL + "/Member/modifyMemberInfo";
        /**
         * 设置头像
         */
        public static final String ACCOUNT_SETAVATAR = INDEX_URL + "/Member/updHeadImg";
        /**
         * 找回密码
         */
        public static final String ACCOUNT_FORGETPASSWORD = INDEX_URL + "/Public/findPassword";
        /**
         * 获取用户的收藏列表
         */
        public static final String ACCOUNT_COLLECT = INDEX_URL + "/Collect/index";
        /**
         * 获取用户的感兴趣用户列表
         */
        public static final String ACCOUNT_INTEREST = INDEX_URL + "/Public/interested";
        /**
         * 获取用户的关注列表
         */
        public static final String ACCOUNT_FOLLOWS = INDEX_URL + "/Follow/myFollows";
        /**
         * 获取用户的粉丝列表
         */
        public static final String ACCOUNT_FANCE = INDEX_URL + "/Follow/myFance";
        /**
         * 获取用户的粉丝列表
         */
        public static final String ACCOUNT_NEW_FRIENDS = INDEX_URL + "/Public/new_friends";
        /**
         * 获取用户的双向关注列表
         */
        public static final String ACCOUNT_EACH_FOLLOWS = INDEX_URL + "/Follow/eachFollows";
        /**
         * 读取用户组
         */
        public static final String ACCOUNT_GROUP = INDEX_URL + "/FollowGroup/lst";
        /**
         * 获取用户相册
         */
        public static final String ACCOUNT_PHOTOS = INDEX_URL + "/Photos/index";
        /**
         * 意见反馈
         */
        public static final String FEEDBACK = INDEX_URL + "/Suggest/index";
        /**
         * 搜索用户
         */
        public static final String SEARCH_USER = INDEX_URL + "/Search/user_list";
        /**
         * 搜索脉搏
         */
        public static final String SEARCH_MB = INDEX_URL + "/Search/airing_list";
        /**
         * 分组脉搏列表
         */
        public static final String GET_MB_FOR_GROUP = INDEX_URL + "/Airing/getAiringByGid";
        /**
         * 获取数量
         */
        public static final String GET_ITEM_NUM = INDEX_URL + "/Public/getitemNum";
        /**
         * 等级
         */
        public static final String LEVEL_URL = INDEX_URL + "/ShowView/level.html";
        /**
         * 常见问题
         */
        public static final String QUESTION_URL = INDEX_URL + "/ShowView/question.html";
        /**
         * URL配置
         */
        public static final String URL_CONFIG = INDEX_URL + "/Public/getSettings";
        /**
         * 通知接口
         */
        public static final String URL_NOTIFY = INDEX_URL + "/Public/message";
        /**
         * 文章
         */
        public static final String URL_ARTICLE = INDEX_URL + "/Article/getArticelLstByMid";
        /**
         * 文章详情
         */
        public static final String URL_ARTICLE_DETAIL = "http://m.maibo.com/article/";
    }

    /**
     * 网络请求额外参数
     *
     * @author cgy
     */
    public static final class Extras {

    }

    /**
     * XML相关配置的键值
     *
     * @author cgy
     */
    public static final class Preferences {
        /**
         * 是否第一次
         */
        public static final String KEY_IS_FIRST_IN = "KEY_IS_FIRST_IN";
        /**
         * 本地用户头像
         */
        public static final String USER_LOCAL_AVATAR = "USER_LOCAL_AVATAR";
        /**
         * 当前用户信息
         */
        public static final String KEY_CURRENT_USER_INFO = "KEY_CURRENT_USER_INFO";
        /**
         * 当前TOKEN
         */
        public static final String KEY_CURRENT_TOKEN = "KEY_CURRENT_TOKEN";
        /**
         * 融云TOKEN
         */
        public static final String KEY_CURRENT_TOKEN_RONGCLOUD = "KEY_CURRENT_TOKEN_RONGCLOUD";
        /**
         * 当前USIGNATURE
         */
        public static final String KEY_CURRENT_USIGNATURE = "KEY_CURRENT_USIGNATURE";
        /**
         * 上一次的刷新的脉搏数
         */
        public static final String KEY_LAST_WEIBO_COUNT = "KEY_LAST_WEIBO_COUNT";
        /**
         * 获取新消息的时间
         */
        public static final String KEY_NOTIFY_SETTING = "KEY_NOTIFY_SETTING";
        /**
         * 是否开启声音
         */
        public static final String KEY_IS_OPEN_SOUND = "KEY_IS_OPEN_SOUND";
        /**
         * URL设置
         */
        public static final String KEY_URL_CONFIG = "KEY_URL_CONFIG";
        /**
         * 消息通知
         */
        public static final String KEY_NOTIFY = "KEY_NOTIFY";
        /**
         * 主题
         */
        public static final String KEY_IS_NIGHT = "KEY_IS_NIGHT";

    }

    /**
     * 请求方式
     *
     * @author cgy
     */
    public static final class RequestType {
        /**
         * 头部
         */
        public static final int HEAD = 10001;
        /**
         * 底部
         */
        public static final int BOTTOM = 10002;
    }

    /**
     * 广播相关动作
     *
     * @author cgy
     */
    public static final class BroadCastAction {
        /**
         * 改变用户信息模块
         */
        public static final String CHANGE_USER_BLOCK = "CHANGE_USER_BLOCK";
        /**
         * 更新脉搏列表
         */
        public static final String REFRESH_MB_LIST = "REFRESH_MB_LIST";
        /**
         * 更新消息数量
         */
        public static final String REFRESH_MESSAGE = "REFRESH_MESSAGE";
        /**
         * 更新用户信息
         */
        public static final String REFRESH_USERINFO = "REFRESH_USERINFO";

    }

}
