package com.ccservice.b2b2c.atom.servlet.tuniu.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.SignUtil;
import com.tenpay.util.MD5Util;

public class TuNiuZhanZuoTest {

    public static void main(String[] args) {
        String jsonString = "{\"sign\":\"a898ae09cf1de07ee39ac38605605b79\",\"timestamp\":\"2016-05-10 12:55:52\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUGXWDdHjhyTPSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc0HiNOVe_Q1ky8T9E5ewQkedwPGYPJrXzKztn_teW9fvVdfWyYcbRr7do5usbZGPVH48kGxdjIazm3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4Md2roVdclKzUxkSy6YTHTOnourUF_a3h8uVRzvEM4pv09HUXGxGJdejlW6JrIbjgctNNXZmNG0Wyo8qW0PUFbOWFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQ9x3YQKvKEuqIEaKJ9mQI4VRfmyiSjMcW55cy7ls1cIPfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4ksX0BuUDRtK-k3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9DU1Rv8Pko0tZqxCmkGcarQSEJ45_uEKGsiJYp4W8LhPm15rjORjiZeVZfMIFeuvnRkPm35aNLYcCRSL0mZ-Q5Is_taaQwFkZa6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR5KSgX49HyblH8CK_K8E1CU\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        String jsonString = "{\"sign\":\"230ec3194acc53ce15a33619749f4300\",\"timestamp\":\"2016-05-10 11:38:48\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUGXWDdHjhyTPSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc0HiNOVe_Q1ky8T9E5ewQkedwPGYPJrXzKztn_teW9fvBQpdUtArETgz2GNwNbDHgvXbMrVk7BUbm3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4Md2roVdclKzUxkSy6YTHTOnourUF_a3h8uVRzvEM4pv09HUXGxGJdejlW6JrIbjgctNNXZmNG0Wyo8qW0PUFbOWFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQ9x3YQKvKEuqIEaKJ9mQI4VRfmyiSjMcW4IHU8aLT_ohfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4ksX0BuUDRtK-k3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9DCq6MGNPjWveMTO2xQxKu1CEJ45_uEKGsiJYp4W8LhPm7ACZcYoSp7zv_VnYZCeS6kPm35aNLYcCRSL0mZ-Q5Is_taaQwFkZa6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR5KSgX49HyblH8CK_K8E1CU\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        String jsonString = "{\"sign\":\"e4b9ec2342cc38ec503aa6d790e01c83\",\"timestamp\":\"2016-05-09 19:52:13\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUGXWDdHjhyTPSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc0HiNOVe_Q1ky8T9E5ewQkedwPGYPJrXzKztn_teW9fvCOgkixABFCf0fg46PcdT6VH3CJ-rsUwOm3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4Md2roVdclKzUxkSy6YTHTOnourUF_a3h8uVRzvEM4pv09HUXGxGJdejlW6JrIbjgctNNXZmNG0Wyo8qW0PUFbOWFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcW9YND5tIdY4RfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4ksX0BuUDRtK-k3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9DuPavVBsLf32Ya8pDXBAUMiEJ45_uEKGsiJYp4W8LhPnkUbjwPPM3_6VO54J8CwDWkPm35aNLYcCRSL0mZ-Q5Is_taaQwFkZa6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR5KSgX49HyblH8CK_K8E1CU\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        String jsonString = "{\"sign\":\"35c61cc26bb5a472cac53f2e8454839d\",\"timestamp\":\"2016-05-09 19:00:52\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUGXWDdHjhyTPSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc0HiNOVe_Q1ky8T9E5ewQkedwPGYPJrXzKztn_teW9fvH1lauvO4bVIC50dEwlvk-FH3CJ-rsUwOm3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4Md2roVdclKzUxkSy6YTHTOnourUF_a3h8uVRzvEM4pv09HUXGxGJdejlW6JrIbjgctNNXZmNG0Wyo8qW0PUFbOWFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcW9YND5tIdY4RfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4ksX0BuUDRtK-k3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9D69yks64Wd6oTpllGmVlwoCEJ45_uEKGsiJYp4W8LhPm_TB4tKNopKzVGcR_20MblkPm35aNLYcCRSL0mZ-Q5Is_taaQwFkZa6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR5KSgX49HyblH8CK_K8E1CU\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        // String jsonString = "{\"sign\":\"c91fe0c6b4fa67fefdcd982fa59c2494\",\"timestamp\":\"2016-05-09 12:18:37\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUGXWDdHjhyTPSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc0HiNOVe_Q1ky8T9E5ewQkedwPGYPJrXzKztn_teW9fvQJc0KQptCwZXdCIOY2RqKJ3UyzjJI181m3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4Md2roVdclKzUxkSy6YTHTOnourUF_a3h8uVRzvEM4pv09HUXGxGJdejlW6JrIbjgctNNXZmNG0Wyo8qW0PUFbOWFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcW6jrLUD9dX9xfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4ksX0BuUDRtK-k3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9DQO774Sw1aOHVhLvvSrs-rSEJ45_uEKGsiJYp4W8LhPnecl2O6VRcPf_-_ezlcRI-kPm35aNLYcCRSL0mZ-Q5Is_taaQwFkZa6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR5KSgX49HyblH8CK_K8E1CU\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        String jsonString = "{\"sign\":\"c575b0ddd2a7b01164314343fd2cba9c\",\"timestamp\":\"2016-05-09 11:28:29\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUGXWDdHjhyTPSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc0HiNOVe_Q1ky8T9E5ewQkedwPGYPJrXzKztn_teW9fvGn5rvSEW0d_Q7ImZXkOY0ca1dE_hqKASm3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4Md2roVdclKzUxkSy6YTHTOnourUF_a3h8uVRzvEM4pv09HUXGxGJdejlW6JrIbjgctNNXZmNG0Wyo8qW0PUFbOWFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcW8iezdunyAH4fhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4ksX0BuUDRtK-k3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9DwX0VxgknkStpauRR0no6ICEJ45_uEKGsiJYp4W8LhPlg9s98ET19xpSX_krtxRBHkPm35aNLYcCRSL0mZ-Q5Is_taaQwFkZa6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR5KSgX49HyblH8CK_K8E1CU\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        String jsonString = "{\"sign\":\"8670baa608ca470a4a176a24aeb8b583\",\"timestamp\":\"2016-05-09 10:45:20\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUGXWDdHjhyTPSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc0HiNOVe_Q1ky8T9E5ewQkedwPGYPJrXzKztn_teW9fvFeN0fQqEz6J9yhSDkfIls53UyzjJI181m3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4Md2roVdclKzUxkSy6YTHTOnourUF_a3h8uVRzvEM4pv09HUXGxGJdejlW6JrIbjgctNNXZmNG0Wyo8qW0PUFbOWFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcW6jrLUD9dX9xfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4ksX0BuUDRtK-k3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9DZFLQvLgyT1XVy_4vVZ20KCEJ45_uEKGsiJYp4W8LhPklLbseFQMTz3AsWZL4YFRikPm35aNLYcCRSL0mZ-Q5Is_taaQwFkZa6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR5KSgX49HyblH8CK_K8E1CU\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        String jsonString = "{\"sign\":\"967160b22852142e69f1ae365824eb35\",\"timestamp\":\"2016-05-07 12:31:28\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUEk_dUkiQrhYSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc0HiNOVe_Q1ky8T9E5ewQkedwPGYPJrXzKztn_teW9fvTtWKShHzAB_xdfxAU9mk8C_tCLzR6y4am3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4Md2roVdclKzUxkSy6YTHTOnourUF_a3h8uVRzvEM4pv09HUXGxGJdejlW6JrIbjgctNNXZmNG0Wyo8qW0PUFbOWFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcW2hjcTY9l1fKfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4kuL61fMiNSDjk3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9D8GpzChC3sTnUfcCeNCJ4NyEJ45_uEKGsiJYp4W8LhPnN1XWTd9LpnK66533WM3UfkPm35aNLYcCRSL0mZ-Q5IkRFPgihpU-K6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR5KSgX49HyblH8CK_K8E1CU\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";

        //        String jsonString = "{\"sign\":\"5170cd49fa03dd16266cbbf0853245f7\",\"timestamp\":\"2016-05-07 11:13:34\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUEk_dUkiQrhYSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc1f-4gJnBVrwKSbS-PSl1emR7RxUQxWLfqztn_teW9fvhSoMdNWgRznPYMGgnNXuoi_tCLzR6y4am3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4Md2roVdclKzVnONEWPKXUO3ourUF_a3h8uVRzvEM4pv0nVt-_cF-u-NpQsLNAQ7hbJfzr9-m2sOlUaBOa2eQLw2FcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcW2hjcTY9l1fKfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4kuL61fMiNSDjk3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9DkMgmlxOmyIgjRgvMjC4LKSEJ45_uEKGsiJYp4W8LhPnMDWNwTyp14TEf2-7nd1lUkPm35aNLYcCRSL0mZ-Q5IkRFPgihpU-K6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR4yuIeUK_J6GFgd9wcMZcac\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        String jsonString = "{\"sign\":\"01256b9b5565c8b43f19eed7e371be54\",\"timestamp\":\"2016-05-07 09:50:25\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUEk_dUkiQrhYSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc2_Rw0Kc7XFgspez_fE6SCJfn7bii4cqz6ztn_teW9fvz5dRtWYYVBouQo9c_yuvpUO6HR7jAkU_m3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4MtvvJYWDSB8PjlYVLD49uC3ourUF_a3h8uVRzvEM4pv2g8daRBpQTk8zX8bSRg7kBVNcZd19KcpHOa-d5V0yYbmFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcWzGQMinHGodTfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4kuL61fMiNSDjk3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9DyQ5mIfgFsmU0r_TUQpWOYSEJ45_uEKGsiJYp4W8LhPnVpoFr_--c5m0K4LNBtNe2kPm35aNLYcCRSL0mZ-Q5IkRFPgihpU-K6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR_sX0Ixni9-E8AZ7lGSnqbM\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        String jsonString = "{\"sign\":\"3a4e96528d522315c88d7130d6933c56\",\"timestamp\":\"2016-05-07 09:00:57\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUEk_dUkiQrhYSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc2_Rw0Kc7XFgspez_fE6SCJfn7bii4cqz6ztn_teW9fvhq0GCMxDGHOnLEwvPt-gxWjD3stV6omsm3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4MtvvJYWDSB8PjlYVLD49uC3ourUF_a3h8uVRzvEM4pv2g8daRBpQTk8zX8bSRg7kBVNcZd19KcpHOa-d5V0yYbmFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcW4u2omRot51gfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4kuL61fMiNSDjk3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9DN6zWH6IItJF-fJ3JAV-ANCEJ45_uEKGsiJYp4W8LhPmKLHisIqnWGTVUV55bVVB0kPm35aNLYcCRSL0mZ-Q5IkRFPgihpU-K6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR_sX0Ixni9-E8AZ7lGSnqbM\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        String jsonString = "{\"sign\":\"eac24a7dc468249c778656738db9b08f\",\"timestamp\":\"2016-05-07 08:09:12\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUEk_dUkiQrhYSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc2_Rw0Kc7XFgspez_fE6SCJfn7bii4cqz6ztn_teW9fvlNGDczISPfOYDxZqlI9Ap1POqWbwMcS7m3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4MtvvJYWDSB8PjlYVLD49uC3ourUF_a3h8uVRzvEM4pv2g8daRBpQTk8zX8bSRg7kBVNcZd19KcpHOa-d5V0yYbmFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcW0qgVC-yRb6ifhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4kuL61fMiNSDjk3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9D2NxGimfXba1hC117pe42yCEJ45_uEKGsiJYp4W8LhPkZgw_HBS8zDnN907RPQIqUkPm35aNLYcCRSL0mZ-Q5IkRFPgihpU-K6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR_sX0Ixni9-E8AZ7lGSnqbM\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        String jsonString = "{\"sign\":\"286904eb1231b352d0b4691c453fcff3\",\"timestamp\":\"2016-05-07 07:12:19\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUEk_dUkiQrhYSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc2_Rw0Kc7XFgspez_fE6SCJfn7bii4cqz6ztn_teW9fvPmoW8_Ef4iVP42Ug_HqMW7EOo2gRlCQIm3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4MtvvJYWDSB8PjlYVLD49uC3ourUF_a3h8uVRzvEM4pv2g8daRBpQTk8zX8bSRg7kBVNcZd19KcpHOa-d5V0yYbmFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcW_7aX6NICrVUfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4kuL61fMiNSDjk3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9D-LtqQbf93O66aZVSIS6SQyEJ45_uEKGsiJYp4W8LhPmqOTJx1xJaSYldShblXK79kPm35aNLYcCRSL0mZ-Q5IkRFPgihpU-K6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR_sX0Ixni9-E8AZ7lGSnqbM\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        String jsonString = "{\"sign\":\"ebf05b1b1600a136ae7f09713343d80f\",\"timestamp\":\"2016-05-06 20:22:12\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUEk_dUkiQrhYSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc2_Rw0Kc7XFgspez_fE6SCJfn7bii4cqz6ztn_teW9fvvcXSGqjdwhkD-oouHudLhYnF4TN7lpNRm3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4MtvvJYWDSB8PjlYVLD49uC3ourUF_a3h8uVRzvEM4pv2g8daRBpQTk8zX8bSRg7kBVNcZd19KcpHOa-d5V0yYbmFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcW95NLQgykmZpfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4kuL61fMiNSDjk3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9D3gYqGAmutGUh-Sc3MOZIryEJ45_uEKGsiJYp4W8LhPnoLR8EBqRiBRAIMr7OYYOVkPm35aNLYcCRSL0mZ-Q5IkRFPgihpU-K6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR_sX0Ixni9-E8AZ7lGSnqbM\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        //        String jsonString = "{\"sign\":\"704bb0a3514416e77ecaaf4e627ba65d\",\"timestamp\":\"2016-05-06 11:50:27\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUEk_dUkiQrhYSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc1f-4gJnBVrwKSbS-PSl1emR7RxUQxWLfqztn_teW9fvYfKBWoh4PsD0UgiVate6sTSTplACkOBTm3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4Md2roVdclKzVnONEWPKXUO3ourUF_a3h8uVRzvEM4pv0nVt-_cF-u-NpQsLNAQ7hbJfzr9-m2sOlUaBOa2eQLw2FcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcWw7GN5T5ULXWfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4kuL61fMiNSDjk3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9DqDpKV7Duc4SHd9S8JD_BTiEJ45_uEKGsiJYp4W8LhPmIjk-we5a3U5ic0X_tFvJpkPm35aNLYcCRSL0mZ-Q5IkRFPgihpU-K6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR4yuIeUK_J6GFgd9wcMZcac\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        //        String jsonString = "{\"sign\":\"607d5805c80c98ac73b13b844b79e125\",\"timestamp\":\"2016-05-06 11:14:08\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUEk_dUkiQrhYSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc2_Rw0Kc7XFgspez_fE6SCJfn7bii4cqz6ztn_teW9fvcGZZTZjgYQgVdsy5OfdovZRZqMBJdm4pm3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4MtvvJYWDSB8PjlYVLD49uC3ourUF_a3h8uVRzvEM4pv2g8daRBpQTk8zX8bSRg7kBVNcZd19KcpHOa-d5V0yYbmFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcWzWD9QS-juCNfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4kuL61fMiNSDjk3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9D_LDg2mFUJPf305UAO98rsiEJ45_uEKGsiJYp4W8LhPldejOitt1_-kKXq35aLcuKkPm35aNLYcCRSL0mZ-Q5IkRFPgihpU-K6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR_sX0Ixni9-E8AZ7lGSnqbM\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        //                String jsonString = "{\"sign\":\"3be02af346916119944fcfa55090aab3\",\"timestamp\":\"2016-05-06 10:33:41\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUEk_dUkiQrhYSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc1f-4gJnBVrwKSbS-PSl1emR7RxUQxWLfqztn_teW9fvTbWl-O8qSiEUoMsRWIUv9jSTplACkOBTm3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4Md2roVdclKzVnONEWPKXUO3ourUF_a3h8uVRzvEM4pv0nVt-_cF-u-NpQsLNAQ7hbJfzr9-m2sOlUaBOa2eQLw2FcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcWw7GN5T5ULXWfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4kuL61fMiNSDjk3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9D8Lh0Cm8S-LsNkvquehO8siEJ45_uEKGsiJYp4W8LhPlYHLEVS6QrYtUB-xFLJZxQkPm35aNLYcCRSL0mZ-Q5IkRFPgihpU-K6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR4yuIeUK_J6GFgd9wcMZcac\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        //        //        String jsonString = "{\"sign\":\"bd5eb955126df088670965cb6737a922\",\"timestamp\":\"2016-05-05 18:31:00\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUFKCsTTpRAaWige7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc2_Rw0Kc7XFgspez_fE6SCJfn7bii4cqz6ztn_teW9fvH_CMT4G-AM3Xhd1Ca_jXx1POqWbwMcS7m3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4MtvvJYWDSB8PjlYVLD49uC3ourUF_a3h8uVRzvEM4pv2g8daRBpQTk8zX8bSRg7kBVNcZd19KcpHOa-d5V0yYbmFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQ3KLamgZnSARIEaKJ9mQI4VRfmyiSjMcW0qgVC-yRb6ifhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4kumFqAaYg1ahU3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9DdRX33QEBx6sQt10HtK-utiEJ45_uEKGsiJYp4W8LhPlfmhW4bND2zmkybvmqnm8FkPm35aNLYcCRSL0mZ-Q5IpJ1YPeSiSpp6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR_sX0Ixni9-E8AZ7lGSnqbM\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        //        //        String jsonString = "{\"sign\":\"db12a8c5cfe07b181c6a798971b847e6\",\"timestamp\":\"2016-05-05 16:12:48\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUFKCsTTpRAaWige7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc0HiNOVe_Q1ky8T9E5ewQkedwPGYPJrXzKztn_teW9fvr65OREbONfPG4gi1B9-KD9pgvq_pAieXm3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4Md2roVdclKzUxkSy6YTHTOnourUF_a3h8uVRzvEM4pv09HUXGxGJdejlW6JrIbjgctNNXZmNG0Wyo8qW0PUFbOWFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQ9x3YQKvKEuqIEaKJ9mQI4VRfmyiSjMcWz2gP6PCq6FlfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4kumFqAaYg1ahU3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9DR0V9h8nYZt4d-u-l3T79sSEJ45_uEKGsiJYp4W8LhPlB27eyp7fyOiK6Zyj6M-vGkPm35aNLYcCRSL0mZ-Q5IpJ1YPeSiSpp6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR5KSgX49HyblH8CK_K8E1CU\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        //        //        String jsonString = "{\"sign\":\"284cc81407290ed8be11cc0f91cb91d0\",\"timestamp\":\"2016-05-05 15:23:47\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUFKCsTTpRAaWige7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc2_Rw0Kc7XFgspez_fE6SCJfn7bii4cqz6ztn_teW9fvKr-kJNRjOzFWpYx5EKfDbNNOzlqu9mzKm3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4MtvvJYWDSB8PjlYVLD49uC3ourUF_a3h8uVRzvEM4pv2g8daRBpQTk8zX8bSRg7kBVNcZd19KcpHOa-d5V0yYbmFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQ9x3YQKvKEuqIEaKJ9mQI4VRfmyiSjMcWxiS2qTuJeeXfhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4kumFqAaYg1ahU3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9DL7t5bcD7kFigj_9p5NRrnCEJ45_uEKGsiJYp4W8LhPl0zWErapzkBmLmtQ11oL8gkPm35aNLYcCRSL0mZ-Q5IpJ1YPeSiSpp6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR_sX0Ixni9-E8AZ7lGSnqbM\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";
        //        SendPostandGet.submitGet2("http://localhost:8080/cn_interface/TuNiuZhanZuoCallBackTestServlet", jsonString,
        //                "UTF-8");
        //        //
        TuNiuZhanZuoTest test = new TuNiuZhanZuoTest();
        System.out.println(test.zhanzuo());

        //
        //        ;
        //        JSONObject json = new JSONObject();
        //        json.put("account", "1232131dfsdsa");
        //        json.put("timestamp", "fdsaf465sadf13c21");
        //        String key = "f65ds1fsd1a65f4sa";
        //        System.out.println(SignUtil.generateSign(json.toString(), key));
        //A66634A470129334A771C985A6812B0C
        //A66634A470129334A771C985A6812B0C
    }

    /**
     * 途牛占座请求测试
     */
    JSONObject json = new JSONObject();

    //
    //    public String account = "yitong_test";
    //
    //    public String key = "6vogatwqvjd64mbz1qx756zj7169trte";

    public String account = "tuniu_basetest";

    public String key = "ix7xk7exkt4c7nd2u62254n51k2vnuzm";

    //    public String account = "tuniulvyou_bes";
    //
    //    public String key = "scc21yt7zm6b23g49n5b17w2ftlioqle";

    String timestamp = "2015-08-03 00:00:00";

    public String zhanzuo() {
        json.put("account", account);
        json.put("timestamp", timestamp);
        String resultString = "";

        //        executepool(33292562, null, "张三", "341222197608254405", "1", "二代身份证", "1", "成人票", "1", "硬座", null, "1234", 0,
        //                null, null, null, null, null, null, null, null, null, null, "testVendorOrderId", "K1237", "BZH", "亳州",
        //                "RZH", "温州", "2015-08-29", "{途牛URL}/train /bookOrderFeedback", "张三", "13728784623", "123");
        //                executepool(1, null, "谢达书", "4600003199005124439", "1", "二代身份证", "1", "成人票", "1", "硬座", null, "1234", 0, null,
        //                        null, null, null, null, null, null, null, null, null, "testVendorOrderId", "K1237", "BZH", "亳州", "RZH",
        //                        "温州", "2016-05-06 00:00", "{途牛URL}/train /bookOrderFeedback", "谢达书", "13728784623", "123");
        //        executepool(158, null, "朱美菊", "460003199005124439", "1", "二代身份证", "1", "成人票", "1", "硬座", null, "1234.0", 0,
        //                null, null, null, null, null, null, null, null, null, null, "100", "K2501", "TYV", "太原", "ZZF", "郑州",
        //        //                "2016-05-09 09:08", "{途牛URL}/train /bookOrderFeedback", "朱美菊", "13728784623", "123");
        //        executepool(30797, null, "廖瑞丹", "654226199106090228", "1", "二代身份证", "1", "成人票", "1", "硬座", null, "88.0", 0,
        //                null, null, null, null, null, null, null, null, null, null, "24986", "K2501", "TYV", "太原", "ZZF", "郑州",
        //                "2016-05-11 09:08", "{途牛URL}/train /bookOrderFeedback", "廖瑞丹", "13728784623", "123");
        //        executepool(18185, null, "何文娟", "340825198608290428", "1", "二代身份证", "1", "成人票", "1", "硬座", null, "88.0", 0,
        //                null, null, null, null, null, null, null, null, null, null, "14827", "K2501", "TYV", "太原", "ZZF", "郑州",
        //                "2016-05-09 09:08", "{途牛URL}/train /bookOrderFeedback", "何文娟", "13728784623", "123");
        //        executepool(31222, null, "刘建东", "372526198404032037", "1", "二代身份证", "1", "成人票", "1", "硬座", null, "88.0", 0,
        //                null, null, null, null, null, null, null, null, null, null, "25308", "K2501", "TYV", "太原", "ZZF", "郑州",
        //                "2016-05-09 09:08", "{途牛URL}/train /bookOrderFeedback", "刘建东", "13728784623", "123");
        //        executepool(31240, null, "赵彩娜", "440582199610104961", "1", "二代身份证", "1", "成人票", "1", "硬座", null, "88.0", 0,
        //                null, null, null, null, null, null, null, null, null, null, "25326", "K2501", "TYV", "太原", "ZZF", "郑州",
        //                "2016-05-06 09:08", "{途牛URL}/train /bookOrderFeedback", "赵彩娜", "13728784623", "123");
        //                executepool(20160504260901150, null, " 胡天", "511524198407176311", "1", "二代身份证", "1", "成人票", "1", "硬座", null, "88.0", 0,
        //                        null, null, null, null, null, null, null, null, null, null, "25437", "K2501", "TYV", "太原", "ZZF", "郑州",
        //                        "2016-05-06 09:08", "{途牛URL}/train /bookOrderFeedback", "赵彩娜", "13728784623", "123");
        String orderId = UUID.randomUUID().toString();
        executepool(31404, null, "杨荣强", "140622199210264251", "1", "二代身份证", "1", "成人票", "1", "硬座", null, "6.0", 0,
                null, null, null, null, null, null, null, null, null, null, orderId, "2672", "TYV", "太原", "TVD", "太原东",
                "2016-06-10", "{tuniuURL}/train /bookOrderFeedback", "杨荣强", "15210909203", "123");
        json.put("sign", SignUtil.generateSign(json.toString(), key));
        resultString = SendPostandGet.submitPost("http://120.26.83.131:9022/cn_interface/train/book", json.toString(),
                "UTF-8").toString();

        //        json.put("sign", SignUtil.generateSign(json.toString(), key));
        //        resultString = SendPostandGet.submitPost("http://localhost:8080/cn_interface/TuNiuTrainZhanZuoServlet",
        //                json.toString(), "UTF-8").toString();
        return resultString;
    }

    private JSONObject executepool(int passengerId, String ticketNo, String passengerName, String passportNo,
            String passportTypeId, String passportTypeName, String piaoType, String piaoTypeName, String zwCode,
            String zwName, String cxin, String price, int reason, String provinceCode, String schoolCode,
            String schoolName, String studentNo, String schoolSystem, String enterYear,
            String preferenceFromStationName, String preferenceFromStationCode, String preferenceToStationName,
            String preferenceToStationCode, String orderId, String cheCi, String fromStationCode,
            String fromStationName, String toStationCode, String toStationName, String trainDate, String callBackUrl,
            String contact, String phone, String insureCode) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject passengers = new JSONObject();

        passengers.put("passengerId", passengerId);
        passengers.put("ticketNo", ticketNo);
        passengers.put("passengerName", passengerName);
        passengers.put("passportNo", passportNo);
        passengers.put("passportTypeId", passportTypeId);
        passengers.put("passportTypeName", passportTypeName);
        passengers.put("piaoType", piaoType);
        passengers.put("piaoTypeName", piaoTypeName);
        passengers.put("zwCode", zwCode);
        passengers.put("zwName", zwName);
        passengers.put("cxin", cxin);
        passengers.put("price", price);
        passengers.put("reason", reason);
        passengers.put("provinceCode", provinceCode);
        passengers.put("schoolCode", schoolCode);
        passengers.put("schoolName", schoolName);
        passengers.put("studentNo", studentNo);
        passengers.put("schoolSystem", schoolSystem);
        passengers.put("enterYear", enterYear);
        passengers.put("preferenceFromStationName", preferenceFromStationName);
        passengers.put("preferenceFromStationCode", preferenceFromStationCode);
        passengers.put("preferenceToStationName", preferenceToStationName);
        passengers.put("preferenceToStationCode", preferenceToStationCode);
        jsonArray.add(passengers);
        jsonObject.put("orderId", orderId);
        jsonObject.put("cheCi", cheCi);
        jsonObject.put("fromStationCode", fromStationCode);
        jsonObject.put("fromStationName", fromStationName);
        jsonObject.put("toStationCode", toStationCode);
        jsonObject.put("toStationName", toStationName);
        jsonObject.put("trainDate", trainDate);
        jsonObject.put("callBackUrl", callBackUrl);
        jsonObject.put("passengers", jsonArray);
        jsonObject.put("contact", contact);
        jsonObject.put("phone", phone);
        jsonObject.put("insureCode", insureCode);
        try {
            String jsonString = TuNiuDesUtil.encrypt(jsonObject.toString());
            json.put("data", jsonString);
            //            String sign = ElongHotelInterfaceUtil.MD5(
            //                    key + "account" + account + "data" + jsonString + "timestamp" + timestamp + key).toUpperCase();
            //            json.put("sign", sign);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 途牛异步出参
     */
    private JSONObject executepool_yibu(int reason, String price, int passengerId, String ticketNo, String zwCode,
            String cxin, String passportTypeName, String passportNo, String zwName, String piaoType,
            String passengerName, String passportTypeId, String piaoTypeName, String vendorOrderId, String orderId,
            Boolean orderSuccess, int orderAmount, String cheCi, String fromStationCode, String fromStationName,
            String toStationCode, String toStationName, String trainDate, String startTime, String arriveTime,
            String orderNumber, String timestamp, int returnCode, String errorMsg) {
        JSONObject jsonObject = new JSONObject();
        JSONObject passengers = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        passengers.put("reason", reason);
        passengers.put("price", price);
        passengers.put("passengerId", passengerId);
        passengers.put("ticketNo", ticketNo);
        passengers.put("zwCode", zwCode);
        passengers.put("cxin", cxin);
        passengers.put("passportTypeName", passportTypeName);
        passengers.put("passportNo", passportNo);
        passengers.put("zwName", zwName);
        passengers.put("piaoType", piaoType);
        passengers.put("passengerName", passengerName);
        passengers.put("passportTypeId", passportTypeId);
        passengers.put("piaoTypeName", piaoTypeName);
        jsonArray.add(passengers);
        jsonObject.put("vendorOrderId", vendorOrderId);
        jsonObject.put("orderId", orderId);
        jsonObject.put("orderSuccess", orderSuccess);
        jsonObject.put("orderAmount", orderAmount);
        jsonObject.put("cheCi", cheCi);
        jsonObject.put("fromStationCode", fromStationCode);
        jsonObject.put("fromStationName", fromStationName);
        jsonObject.put("toStationCode", toStationCode);
        jsonObject.put("toStationName", toStationName);
        jsonObject.put("trainDate", trainDate);
        jsonObject.put("startTime", startTime);
        jsonObject.put("arriveTime", arriveTime);
        jsonObject.put("orderNumber", orderNumber);
        jsonObject.put("passengers", jsonArray);
        try {
            String jsonString = TuNiuDesUtil.encrypt(jsonObject.toString());
            json_yibu.put("data", jsonObject);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        json_yibu.put("timestamp", timestamp);
        json_yibu.put("returnCode", returnCode);
        json_yibu.put("errorMsg", errorMsg);
        return jsonObject;
    }

    /**
     * 异步回调测试
     */
    JSONObject json_yibu = new JSONObject();

    public void yibucanshu() {
        json_yibu.put("account", "testAccount");
        JSONObject jsonObject = new JSONObject();
        TuNiuZhanZuoTest test = new TuNiuZhanZuoTest();
        jsonObject = test.executepool_yibu(0, "152.5", 33292562, "E0104002221053148", "1", "05车厢,无座", "二代身份证",
                "341222197608254405", "硬座", "1", "张三", "1", "成人票", "testVendorOrderId", "AC9WSY4H1H00", true, 305,
                "K1237", "BZH", "亳州", "RZH", "温州", "2015-08-29", "15:57:00", "11:40:00", "E010400222",
                "2015-08-03 00:00:00", 231000, "");
        System.out.println(zhanzuohuidiao(jsonObject.toString()));
    }

    /**
     * md5(partnerid+method+reqtime+md5(key))，
     * 
     * @time 2014年12月12日 下午2:44:31
     * @author chendong
     */
    public static String getsign(String partnerid, String reqtime, String key) {
        return MD5Util.MD5Encode(partnerid + reqtime + MD5Util.MD5Encode(key, "UTF-8"), "UTF-8");
    }

    public final static SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static String getreqtime() {
        return yyyyMMddHHmmssSSS.format(new Date());
    }

    /**
     * 途牛占座回调
     */
    public String zhanzuohuidiao(String param) {
        String result = "";
        if (param != null & !"".equals(param)) {
            //            JSONObject json = new JSONObject();
            //            json.put("trainorderid", 3595);
            //            json.put("method", "train_order_callback");
            //            json.put("returnmsg", "");
            //            json.put("sign", SignUtil.generateSign(json.toString(), key));
            result = SendPostandGet.submitPost("http://localhost:8080/cn_interface/tcTrainCallBack", param, "UTF-8")
                    .toString();
        }
        return result;
    }
}
