package com.aj.hyena.service;

import com.aj.hyena.HyenaConstants;
import com.aj.hyena.mapper.PointMapper;
import com.aj.hyena.model.po.PointPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointService {
    private static final Logger logger = LoggerFactory.getLogger(PointService.class);

    @Autowired
    private PointMapper cusPointMapper;

    @Autowired
    private PointTableService cusPointTableService;

    public PointPo getCusPoint(String type, String cusId) {
        String tableName = this.getTableName(type);
        var ret = this.cusPointMapper.getCusPoint(tableName, cusId, false);
        return ret;
    }

    @Transactional
    public PointPo addPoint(String type, String cusId, Long point) {
        logger.info("type = {}, cusId = {}, point = {}", type, cusId, point);
        String tableName = this.cusPointTableService.getOrCreateTable(type);
        logger.debug("tableName = {}", tableName);
        var cusPoint = this.cusPointMapper.getCusPoint(tableName, cusId, true);
        if(cusPoint == null) {
            this.cusPointMapper.addPoint(tableName, cusId, point);
            cusPoint = this.cusPointMapper.getCusPoint(tableName, cusId, false);
        }else {
            cusPoint.setPoint(point);
            this.cusPointMapper.updateCusPoint(tableName, cusPoint);
        }
        return cusPoint;
    }

    private String getTableName(String type) {
        return HyenaConstants.PREFIX_POINT_TABLE_NAME + type;
    }
}