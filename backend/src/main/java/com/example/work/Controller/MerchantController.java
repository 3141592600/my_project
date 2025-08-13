package com.example.work.Controller;

import com.example.work.Service.MerchantService;
import com.example.work.dto.MerchantRegisterDTO;
import com.example.work.entity.master.MerchantRegistry;
import com.example.work.entity.tenant.UserInfo;
import com.example.work.util.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
@CrossOrigin
public class MerchantController {
    private final MerchantService merchantService;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody MerchantRegisterDTO dto){
        merchantService.registerMerchant(dto);
        return Result.success() ;
    }

    /**
     * 分页获取所有商户信息
     * @return
     */
    @GetMapping("/getMerchantAllInfo")
    public Result<PageInfo<MerchantRegistry>> getMerchantAllInfo(@RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "8") int pageSize){

        PageHelper.startPage(pageNum, pageSize); // 内存分页
        List<MerchantRegistry> merchantRegistryList = merchantService.getAllMerchants();
        return Result.success(new PageInfo<>(merchantRegistryList));
    }
}
