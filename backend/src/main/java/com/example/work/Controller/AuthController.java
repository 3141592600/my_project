package com.example.work.Controller;

import com.example.work.Service.AuthService;
import com.example.work.Service.MerchantService;
import com.example.work.dto.*;
import com.example.work.entity.tenant.DeviceInfo;
import com.example.work.entity.tenant.UserInfo;
import com.example.work.util.JwtUtil;
import com.example.work.util.PasswordUtil;
import com.example.work.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录控制器
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MerchantService merchantService;

    private final JwtUtil jwtUtil;

    /**
     * 登录
     * @param requestDTO
     * @return
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginRequestDTO requestDTO) {
        String token = authService.login(requestDTO);
        if  (token.equals("密码错误")) {
            return Result.error("密码错误");
        }
        if (token.equals("用户名不存在")) {
            return Result.error("用户名不存在");
        }
        return Result.success(token);
    }

    @GetMapping("/info")
    public Result<UserInfo> getUserInfo(HttpServletRequest request) {
        String token = getToken(request);
        String username = jwtUtil.parseMerchantToken(token).getSubject();
        UserInfo userInfo = authService.getUserInfo(username);
        return Result.success(userInfo);
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/update")
    public Result<?> updateUserInfo(HttpServletRequest request,@RequestBody UpdateUserInfoDTO updateUserInfoDTO) {
        String token = getToken(request);
        String username = jwtUtil.parseMerchantToken(token).getSubject();
        boolean result = authService.updateUserInfo(username,updateUserInfoDTO);
        return result ? Result.success() : Result.error("更新失败");
    }

    /**
     * 新建子用户
     */
    @PostMapping("/add_user")
    public Result<?> addUser(
            @RequestBody UserInfoDTO userInfo,
            HttpServletRequest request) {
        String token = getToken(request);
        String merchant_id = jwtUtil.getMerchantIdFromToken(token);
        String result = authService.addUser(userInfo,merchant_id);
        switch (result) {
            case "用户名已存在":
                return Result.error(result);
            case "添加用户失败":
                return Result.error(result);
            case "添加用户权限失败":
                return Result.error(result);
            default:
                return Result.success("添加成功");
        }
    }

    /**
     * 字段级控制，设备信息表
     */
    @PostMapping("/setPermissions")
    public Result<?> setPermissions(@RequestBody PermissionRequest request) {
        String result = authService.setPermissions(request);
        if (result.equals("字段权限转换JSON失败")) {
            return Result.error("字段权限转换JSON失败");
        }
        else if (result.equals("该设备不存在")) {
            return Result.error("该设备不存在");
        }
        return Result.success(result);
    }

    /**
     * 子用户看设备信息xxxxxxxxxxxxxxxxxxxxxxx
     */
    @GetMapping("/getAllDevices")
    public Result<?> listDevices(HttpServletRequest request) {
        String token = getToken(request);
        String username = jwtUtil.parseMerchantToken( token).getSubject();
        Map<String, List<Map<String, Object>>> data = authService.getUserDeviceData(username);
        return Result.success(data);
    }

    /**
     * 修改密码xxxxxxxxxxxxxxxxxxxxxxx
     */
    @PutMapping("/change_password")
    public Result<?> changePassword(HttpServletRequest request,
                                    @RequestParam String oldPassword,
                                    @RequestParam String newPassword) {
        String token = getToken(request);
        String username = jwtUtil.parseMerchantToken(token).getSubject();
        String merchant_id = jwtUtil.getMerchantIdFromToken(token);
        // 判断旧密码是否与数据库保存密码相同
        if (!PasswordUtil.matches(oldPassword, authService.getPasswordByUsername(username))) {
            return Result.error("原密码输入错误");
        }
        boolean result = authService.changePassword(username, oldPassword, newPassword);
        return result ? Result.success("密码修改成功") : Result.error("修改密码失败");
    }

    /**
     * 获取全部设备最新使用信息
     */
    @GetMapping("/AlldeviceInfo")
    public Result<?> getAllDeviceInfo() {
        List<AllDeviceDTO> allDeviceInfo = authService.getAllDeviceInfo();
        return allDeviceInfo == null ? Result.error("商户不存在") : Result.success(allDeviceInfo);
    }

    /**
     * 获取某设备最新使用信息
     */
    @GetMapping("/deviceInfo")
    public Result<?> getDeviceInfo(String deviceName) {
        DeviceInfo DeviceInfo = authService.getDeviceInfo(deviceName);
        // 按照 {tableName: devices} 格式返回
        Map<String, Object> result = new HashMap<>();
        result.put("deviceName", deviceName);
        result.put("data", DeviceInfo);
        return DeviceInfo == null ? Result.error("设备不存在") : Result.success(result);
    }

    /**
     * 从请求头中提取 token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        throw new RuntimeException("Token 不存在或格式错误");
    }


}
