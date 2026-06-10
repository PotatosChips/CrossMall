package edu.cafuc.crossmall.service;

import edu.cafuc.crossmall.pojo.Merchant;
import edu.cafuc.crossmall.pojo.vo.MerchantVO;

import java.util.List;

public interface MerchantService {
    Integer insertMerchant(Merchant merchant);
    Long selectMerchantIdByUserId(Long userId);
    List<String> selectAllRegions();
    List<MerchantVO> selectShopList(String region, String keyword, Integer page, Integer pageSize);
    Integer countShops(String region, String keyword);
    MerchantVO selectShopById(Long id);
}
