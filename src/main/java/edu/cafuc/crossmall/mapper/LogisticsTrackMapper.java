package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.LogisticsTrack;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LogisticsTrackMapper {

    /** 查询某条物流记录下的所有轨迹节点 */
    List<LogisticsTrack> selectByLogisticsId(Long logisticsId);

    /** 插入物流轨迹 */
    Integer insertTrack(LogisticsTrack track);
}
