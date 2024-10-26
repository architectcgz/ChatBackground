package com.example.chatplatform.entity.enums;

/**
 * @author archi
 */

public enum ResponseEnum {

    ALREADY_REGISTERED(1001, "该邮箱已经注册过"),
    CAPTCHA_GENERATE_ERROR(1002, "验证码生成错误"),
    USER_AUTH_ERROR(401, "用户身份验证失败"),
    INTERNAL_ERROR(500,"服务器内部错误"),
    CAPTCHA_VERIFY_ERROR(1003, "用户输入的验证码错误"),
    USER_ACCESS_DENIED(403, "用户权限不足"),
    PARAM_FORMAT_ERROR(1004, "参数格式错误:"),
    USER_NOT_FOUND_ERROR(1005, "用户不存在"),
    RESOURCE_NOT_FOUND_ERROR(404, "用户访问的资源不存在"),
    USER_UID_NOT_FOUND(1006, "当前用户的UID不存在"),
    FILE_FORMAT_ERROR(1007, "文件格式错误"),
    FILE_EMPTY_ERROR(1008, "上传的文件为空"),
    FILE_NAME_EMPTY_ERROR(1009, "上传的文件名为空"),
    FILE_NAME_LENGTH_ERROR(1010, "上传的文件名称过长"),
    FILE_UPLOAD_ERROR(1011, "文件上传失败"),
    AVATAR_FORMAT_ERROR(1012, "上传的头像格式错误(应为jpg,jpeg,bmp或png)"),
    CANNOT_DO_NO_FRIEND(1013, "未添加对方未好友,不能进行操作"),
    CANNOT_ADD_FRIEND(1014, "你被对方拉入黑名单,不能添加对方为好友"),
    NO_FRIEND_REQUEST(1015, "没有收到好友申请,不能添加对方为好友"),
    PARAM_NOT_ENOUGH_ERROR(1016, "缺少请求参数"),
    GROUP_NOT_EXISTS(1017, "该群组不存在"),
    CANNOT_DISMISS_GROUP(1018, "你不是群主,无法解散群聊"),
    ALREADY_IN_GROUP(1019, "你已经加入了该群,无法重复进群"),
    CANNOT_GET_GROUP_REQUEST_LIST(1020, "你不是该群的管理员或群主,无法查看加群请求列表"),
    OPERATION_NOT_VALID(1021, "操作非法"),
    CANNOT_ADD_ADMIN(1022, "你不是该群群主,不能进行该操作"),
    USER_NOT_IN_GROUP(1023, "该用户不在群中,无法设置为管理员"),
    CANNOT_DELETE_ADMIN(1022, "你不是该群群主,无法删除管理员"),
    CANNOT_SEND_MSG(1023, "你被对方拉黑或删除,无法向其发送消息"),
    MSG_NOT_EXIST_ERROR(1024, "消息不存在"),
    MSG_SENDER_ERROR(1025,"这条消息不是由您发送,无法撤回"),
    MESSAGE_TYPE_ERROR(1026,"消息类型错误"),
    CANNOT_SEND_TO_GROUP_NOT_MEMBER(1027,"你不是群成员,无法发送消息"),
    CANNOT_RECALL_FROM_GROUP_NOT_MEMBER(1028,"你不是群成员,无法撤回消息"),
    CAPTCHA_SEND_ERROR(1029,"服务器内部错误,验证码发送失败,请联系服务器管理员" ),
    NO_SUCH_LOGIN_METHOD(1030,"没有这种登录方式"),
    QRCODE_GENERATE_ERROR(1031,"用户二维码生成失败"),
    FETCH_USER_AVATAR_ERROR(1032,"用户头像获取失败" ),
    QRCODE_UPLOAD_ERROR(1033,"用户二维码上传失败" ),
    FRIEND_ID_ERROR(1034,"好友Uid格式错误" ),
    HAS_REQUESTED(1035, "好友请求已经发送,请勿重复请求"),
    FRIEND_REQUEST_EXPIRED(1036, "好友请求已过期，无法添加好友");

    private final int code;
    private final String message;

    ResponseEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
