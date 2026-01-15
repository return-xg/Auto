package com.auto.web.controller.admin;

import com.auto.common.core.controller.BaseController;
import com.auto.common.core.domain.AjaxResult;
import com.auto.common.core.page.TableDataInfo;
import com.auto.domain.Order;
import com.auto.domain.RefundReturn;
import com.auto.domain.vo.OrderVO;
import com.auto.service.IOrderService;
import com.auto.service.IRefundReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/order")
public class AdminOrderController extends BaseController {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IRefundReturnService refundReturnService;

    @GetMapping("/list")
    public TableDataInfo list(OrderVO orderVO) {
        startPage();
        List<OrderVO> list = orderService.getAdminOrderList(orderVO);
        return getDataTable(list);
    }

    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(orderService.getOrderById(id));
    }

    @PostMapping("/deliver")
    public AjaxResult deliver(@RequestParam Long orderId, @RequestParam String logisticsCompany, @RequestParam String logisticsNo) {
        Order order = new Order();
        order.setId(orderId);
        orderService.deliverOrder(orderId);
        return success("发货成功");
    }

    @PostMapping("/auditRefund")
    public AjaxResult auditRefund(@RequestParam Long orderId, @RequestParam Long auditStatus, @RequestParam String auditRemark) {
        RefundReturn refundReturn = refundReturnService.selectRefundReturnByOrderId(orderId);
        if (refundReturn == null) {
            return error("退款申请不存在");
        }
        if (refundReturn.getStatus() != 0) {
            return error("退款申请已审核");
        }
        refundReturn.setStatus(auditStatus);
        refundReturn.setAdminRemark(auditRemark);
        refundReturnService.updateRefundReturn(refundReturn);
        return success("审核成功");
    }
}
