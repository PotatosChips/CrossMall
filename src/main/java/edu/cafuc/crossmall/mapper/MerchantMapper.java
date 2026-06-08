package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.Merchant;
import edu.cafuc.crossmall.pojo.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MerchantMapper {
    Integer insertMerchant(Merchant merchant);

}
