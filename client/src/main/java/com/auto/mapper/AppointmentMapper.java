package com.auto.mapper;

import com.auto.domain.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AppointmentMapper {
    Appointment selectAppointmentById(Long id);

    List<Appointment> selectAppointmentListByOrderId(Long orderId);

    List<Appointment> selectAppointmentListByUserId(Long userId);

    List<Appointment> selectAppointmentList(Appointment appointment);

    int insertAppointment(Appointment appointment);

    int updateAppointment(Appointment appointment);

    int deleteAppointmentById(Long id);

    int deleteAppointmentByIds(Long[] ids);

    int deleteAppointmentByOrderId(Long orderId);
}
