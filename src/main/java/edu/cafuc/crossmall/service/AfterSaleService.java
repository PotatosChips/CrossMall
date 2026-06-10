package edu.cafuc.crossmall.service;

import edu.cafuc.crossmall.pojo.vo.AfterSaleVO;

import java.util.List;

public interface AfterSaleService {

    Integer apply(Long userId, String orderNo, Integer type, String reason);

    List<AfterSaleVO> listByUser(Long userId);

    List<AfterSaleVO> listByOrderNo(String orderNo, Long userId);

    List<AfterSaleVO> listForSeller(Long sellerUserId);

    Integer handle(Long id, Long sellerUserId, Integer status, String reply, String company);
}
