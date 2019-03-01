package com.mb.android.maiboapp.entity;

/**
 * Created by cgy on 15/8/28.
 */
public class RegisterResp extends CommonEntity {
    private ResultData data;

    public ResultData getData() {
        return data;
    }

    public void setData(ResultData data) {
        this.data = data;
    }

    public class ResultData{
        private UserEntity member;

        public UserEntity getMember() {
            return member;
        }

        public void setMember(UserEntity member) {
            this.member = member;
        }
    }
}
