package com.cutejiuge.user.validation;

import com.cutejiuge.user.pojo.dto.OperateVerificationCodeDTO;
import com.cutejiuge.user.pojo.dto.SendVerificationCodeDTO;

public interface VerificationCodeValidation {
    void checkSendVerificationCodeParams(SendVerificationCodeDTO sendVerificationCodeDTO);
    void checkOperateVerificationCodeParams(OperateVerificationCodeDTO operateVerificationCodeDTO);
}
