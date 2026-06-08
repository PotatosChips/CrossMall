package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.mapper.MerchantMapper;
import edu.cafuc.crossmall.pojo.Merchant;
import edu.cafuc.crossmall.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantServiceImpl implements MerchantService {
    @Autowired
    private MerchantMapper merchantMapper;
    @Override
    public Integer insertMerchant(Merchant merchant) {
        return merchantMapper.insertMerchant(merchant);
    }
}
