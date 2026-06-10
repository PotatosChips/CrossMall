package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.mapper.MerchantMapper;
import edu.cafuc.crossmall.pojo.Merchant;
import edu.cafuc.crossmall.pojo.vo.MerchantVO;
import edu.cafuc.crossmall.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MerchantServiceImpl implements MerchantService {
    private static final String REGION_CACHE_KEY = "mall:regions";
    private static final long CACHE_TTL_MINUTES = 10;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    public Integer insertMerchant(Merchant merchant) {
        Integer rows = merchantMapper.insertMerchant(merchant);
        if (rows != null && rows > 0) {
            evictRegionCache();
        }
        return rows;
    }

    @Override
    public Long selectMerchantIdByUserId(Long userId) {
        return merchantMapper.selectMerchantIdByUserId(userId);
    }

    @Override
    public List<String> selectAllRegions() {
        try {
            String cached = redisTemplate.opsForValue().get(REGION_CACHE_KEY);
            if (cached != null) {
                return objectMapper.readValue(cached, new TypeReference<List<String>>() {});
            }
        } catch (Exception ignored) {
        }

        List<String> list = merchantMapper.selectAllRegions();

        try {
            redisTemplate.opsForValue().set(
                    REGION_CACHE_KEY,
                    objectMapper.writeValueAsString(list),
                    CACHE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
        } catch (Exception ignored) {
        }
        return list;
    }

    private void evictRegionCache() {
        redisTemplate.delete(REGION_CACHE_KEY);
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
