package org.example.service;

import org.example.dao.VerificationCodeDao;
import org.example.model.VerificationCode;
import org.example.util.DateUtil;
import org.example.util.VerificationCodeUtil;


public class VerificationCodeService {


    private final VerificationCodeDao dao = new VerificationCodeDao();

    private String lastErrorMessage;

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public  String generateCode(int userId) {

        String otp = VerificationCodeUtil.generateOtp();

        VerificationCode code = new VerificationCode();
        code.setUserId(userId);
        code.setVerificationCode(otp);
        code.setPurpose("SENSITIVE_OPERATION");
        code.setExpiresAt(
                DateUtil.minutesFromNow(5)
        );

        boolean saved = dao.saveCode(code);

        if (!saved) {
            lastErrorMessage = "Failed to generate verification code.";
            return null;
        }

        return otp;
    }

    public boolean verifyCode(int userId, String otp) {

        if (otp == null || otp.trim().isEmpty()) {
            lastErrorMessage = "Verification code is required.";
            return false;
        }

        VerificationCode vc = dao.getValidCode(userId, otp);

        if (vc == null) {
            lastErrorMessage = "Invalid or expired verification code.";
            return false;
        }

        dao.markAsUsed(vc.getCodeId());
        return true;
    }
}
