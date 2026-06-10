package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.Merchant;
import edu.cafuc.crossmall.pojo.vo.MerchantVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MerchantMapper {
    Integer insertMerchant(Merchant merchant);
    Long selectMerchantIdByUserId(Long userId);

    /** 查询所有商家地区（去重，用于筛选/注册下拉） */
    List<String> selectAllRegions();

    /** 分页查询店铺列表 */
    List<MerchantVO> selectShopList(@Param("region") String region,
                                    @Param("keyword") String keyword,
                                    @Param("offset") Integer offset,
                                    @Param("pageSize") Integer pageSize);

    Integer countShops(@Param("region") String region, @Param("keyword") String keyword);

    MerchantVO selectShopById(@Param("id") Long id);
}
