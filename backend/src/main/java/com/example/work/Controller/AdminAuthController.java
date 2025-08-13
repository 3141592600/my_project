package com.example.work.Controller;

import com.example.work.Service.AdminAuthService;
import com.example.work.Service.MerchantService;
import com.example.work.dto.*;
import com.example.work.entity.master.*;
import com.example.work.entity.tenant.DeviceInfo;
import com.example.work.entity.tenant.UserInfo;
import com.example.work.util.JwtUtil;
import com.example.work.util.PasswordUtil;
import com.example.work.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final MerchantService merchantService;
    private final JwtUtil jwtUtil;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody AdminLoginRequestDTO dto) {
        String token = adminAuthService.login(dto);
        if  (token.equals("密码错误")) {
            return Result.error("密码错误");
        }
        if (token.equals("管理员账号不存在")) {
            return Result.error("管理员账号不存在");
        }
        return Result.success(token);
    }

    /**
     * 新建设备信息表
     */
    @PostMapping("/add_device")
    public Result<?> addDevice(@RequestParam String tableName,@RequestParam String merchantId,@RequestBody DeviceInfoDTO deviceInfoDTO) {
        try {
            DeviceInfo deviceInfo = converDeviceInfo(deviceInfoDTO);
            adminAuthService.createTableAndInsert(tableName, merchantId, deviceInfo);
            return Result.success("Table created and device info inserted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("Failed to create table or insert device info");
        }
    }



    /**
     * 编辑设备信息表
     */
    @PostMapping("/edit_device")
    public Result<?> editDevice(@RequestParam String deviceName,
                                @RequestParam String merchantName,
                                @RequestBody DeviceInfoDTO deviceInfoDTO) {
        try{
            DeviceInfo deviceInfo = converDeviceInfo(deviceInfoDTO);
            adminAuthService.editDevice(deviceName,merchantName, deviceInfo);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return Result.error("Failed to edit device info");
        }
        return Result.success("edit device info successfully");
    }

    /**
     * 获取一个公司的全部设备信息
     */
    @GetMapping("/getAllDeviceInfo")
    public Result<?> getAllDeviceInfo(@RequestParam String merchantName) {
        List<AllMerchantDeviceInfoDTO> allDeviceInfo = adminAuthService.getAllDeviceInfo(merchantName);
        return allDeviceInfo == null ? Result.error("商户不存在") : Result.success(allDeviceInfo);
    }

    /**
     * 查询全部公司的全部设备信息（分页：3个公司的全部设备信息为一页）
     */
    @GetMapping("/getAllMerchantsDeviceInfo")
    public Result<?> getAllMerchantsDeviceInfo(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "3") int pageSize) {
        Page<AllMerchantDeviceInfoDTO> page = adminAuthService.getAllCompanyDevices(pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 上传设备信息
     */
    @PostMapping("/upload_deviceInfo")
    public Result<?> uploadDeviceInfo(@RequestParam String tableName,
                                      @RequestParam String merchantId,
                                      @RequestBody DeviceInfoDTO deviceInfoDTO) {
        try{
            DeviceInfo deviceInfo = converDeviceInfo(deviceInfoDTO);
            adminAuthService.insetDeviceInfo(tableName,merchantId, deviceInfo);
        }catch (Exception e){
            return Result.error("Failed to upload device_info");
        }
        return Result.success("upload device_info successfully");
    }

    /**
     * 禁用设备
     */
    @PostMapping("/disable_device")
    public Result<?> disableDevice(@RequestBody DisableDeviceDTO disableDeviceDTO) {
        try{
            adminAuthService.disableDevice(disableDeviceDTO);
            return Result.success("禁用成功");
        }catch (Exception e){
            System.out.println(e.getMessage());
            return Result.error("禁用失败");
        }
    }

    /**
     * 删除设备
     */
    @DeleteMapping("/delete_device")
    public Result<?> deleteDevice(@RequestParam String deviceName,@RequestParam String merchantName) {
        try{
            adminAuthService.deleteDevice(merchantName,deviceName);
        }catch (Exception e){
            return Result.error("Failed to delete device");
        }
        return Result.success("delete device successfully");
    }

    /**
     * :获取某公司某设备信息
     */
    @GetMapping("/getDevice")
    public Result<?> getDevice(@RequestParam String merchantName,@RequestParam String tableName) {
        MerchantRegistry merchant = adminAuthService.findByMerchantName(merchantName);
        String merchantId = merchant.getMerchant_id();
        DeviceInfo devices = adminAuthService.getDevices(merchantId,tableName);
        // 按照 {tableName: devices} 格式返回
        Map<String, Object> result = new HashMap<>();
        result.put("deviceName", tableName);
        result.put("data", devices);
        return devices == null ? Result.error("商户不存在") : Result.success(result);
    }

    /**
     * 修改密码
     */
    @PutMapping("/change_password")
    public Result<?> changePassword(HttpServletRequest request,
                                    @RequestParam String oldPassword,
                                    @RequestParam String newPassword) {
        String token = getToken(request);
        String username = jwtUtil.parseAdminToken(token).getSubject();
        // 判断旧密码是否与数据库保存密码相同
        if (!PasswordUtil.matches(oldPassword, adminAuthService.getPasswordByUsername(username))) {
            return Result.error("原密码输入错误");
        }
        boolean result = adminAuthService.changePassword(username, oldPassword, newPassword);
        return result ? Result.success("密码修改成功") : Result.error("修改密码失败");
    }

    /**
     * 分页获取所有商户(主+子)
     */
    @GetMapping("/getMerchants")
    public Result<?> getAllUsers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "8") int pageSize) {

//        PageHelper.startPage(pageNum, pageSize); // 内存分页
        List<UserInfo> allUsers = adminAuthService.getAllUsers();
        // 手动分页
        int total = allUsers.size();
        int fromIndex = Math.min((pageNum - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);

        List<UserInfo> pageList = allUsers.subList(fromIndex, toIndex);

        // 返回分页结构
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("data", pageList);

        return Result.success(result);
    }

    /**
     * 通过公司id查询商户信息
     */
    @GetMapping("/getMerchantByMerchantName")
    public Result<?> getMerchantByMerchantId(@RequestParam String merchantName) {
        MerchantRegistry merchant = adminAuthService.findByMerchantName(merchantName);
        return merchant == null ? Result.error("商户不存在") : Result.success(merchant);
    }


    /**
     * 通过公司id删除用户信息
     */
    @DeleteMapping("/deleteUserByMerchantID")
    public Result<?> deleteUserByMerchantId(@RequestParam String merchantID) {
        adminAuthService.deleteUserByMerchantId(merchantID);
        return Result.success();
    }


    /**
     * 编辑主用户信息（主库）
     */
    @PutMapping("/update_merchant")
    public Result<?> updateMerchantInfo(@RequestBody UpdateMerchantRegistryDTO merchantInfo) {
        String password = merchantInfo.getPassword();
        String encrypted = PasswordUtil.encrypt(password);
        merchantInfo.setPassword(encrypted);
        boolean success = adminAuthService.updateMerchantInfo(merchantInfo);

        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 获取对应用户数据库的永久JWT 密钥
     */
    @GetMapping("/getToken")
    public Result<String> getMerchantToken(@RequestParam("merchantId") String merchantId) {
        MerchantRegistry merchant = merchantService.getMerchantsByMerchantId(merchantId);
        if (merchant != null ) {
            String token = jwtUtil.generatePermanentMerchantToken(merchantId, merchant.getMerchant_name());
            return Result.success(token);
        }
        return Result.error("认证失败：merchantId 错误");
    }

    /**
     * 转换DeviceInfoDTO为DeviceInfo
     */
    public DeviceInfo converDeviceInfo(DeviceInfoDTO deviceInfoDTO){
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setId(deviceInfoDTO.getId());
        deviceInfo.setDevice_code(deviceInfoDTO.getDevice_code());
        deviceInfo.setDevice_type(deviceInfoDTO.getDevice_type());
        deviceInfo.setLatest_status(deviceInfoDTO.getLatest_status());
        deviceInfo.setTotal_output_quantity(deviceInfoDTO.getTotal_output_quantity());
        deviceInfo.setTotal_input_quantity(deviceInfoDTO.getTotal_input_quantity());
        deviceInfo.setRemaining_quantity(deviceInfoDTO.getRemaining_quantity());
        deviceInfo.setPay_status(deviceInfo.getPay_status());
        return deviceInfo;
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

//    public static void main(String[] args) {
//        String newPassword = PasswordUtil.encrypt("123456"); // 新密码
//        System.out.println(newPassword);
//    }
}
