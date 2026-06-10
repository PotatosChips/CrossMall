package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.mapper.MerchantMapper;
import edu.cafuc.crossmall.pojo.Merchant;
import edu.cafuc.crossmall.pojo.vo.MerchantVO;
import edu.cafuc.crossmall.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantServiceImpl implements MerchantService {
    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    public Integer insertMerchant(Merchant merchant) {
        return merchantMapper.insertMerchant(merchant);
    }

    @Override
    public Long selectMerchantIdByUserId(Long userId) {
        return merchantMapper.selectMerchantIdByUserId(userId);
    }

    @Override
    public List<String> selectAllRegions() {
        return merchantMapper.selectAllRegions();
    }

    @Override
    public List<MerchantVO> selectShopList(String region, String keyword, Integer page, Integer pageSize) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int offset = (safePage - 1) * safePageSize;
        return merchantMapper.selectShopList(region, keyword, offset, safePageSize);
    }

    @Override
    public Integer countShops(String region, String keyword) {
        return merchantMapper.countShops(region, keyword);
    }

    @Override
    public MerchantVO selectShopById(Long id) {
        return merchantMapper.selectShopById(id);
    }
}
