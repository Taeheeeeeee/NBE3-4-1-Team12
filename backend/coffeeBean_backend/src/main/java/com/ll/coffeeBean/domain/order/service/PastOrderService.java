package com.ll.coffeeBean.domain.order.service;

import com.ll.coffeeBean.domain.order.dto.GetResPastOrderDto;
import com.ll.coffeeBean.domain.order.entity.DetailOrder;
import com.ll.coffeeBean.domain.order.entity.PastOrder;
import com.ll.coffeeBean.domain.order.repository.PastOrderRepository;
import com.ll.coffeeBean.domain.siteUser.entity.SiteUser;
import com.ll.coffeeBean.standard.PageDto.PageDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PastOrderService {
    private final PastOrderRepository pastOrderRepository;

    public PageDto<GetResPastOrderDto> getList(SiteUser siteUser, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<PastOrder> paging = pastOrderRepository.findByCustomer(siteUser, pageable);

        Page<GetResPastOrderDto> pagingOrderDto = paging.map(GetResPastOrderDto::new);
        return new PageDto<>(pagingOrderDto);
    }

    @Transactional
    public void processPastOrderByScheduling() {
        LocalDateTime endDate = LocalDateTime.now().minusMonths(3);

        List<PastOrder> pastOrders = pastOrderRepository.findByCreateDateBefore(endDate);

        // 3개월 전까지의 PastOrder 모두 삭제
        for (PastOrder pastOrder : pastOrders) {
            processPastOrder(pastOrder);
        }
    }

    /**
     * 3개월이 지난 주문 목록 삭제
     */
    @Transactional
    public void processPastOrder(PastOrder order) {
        log.info("삭제된 주문");
        log.info("DetailOrders.id: {}, Customer.id: {}", order.getOrders().stream().map(DetailOrder::getId).collect(
                Collectors.toList()), order.getCustomer().getId());

        order.getCustomer().removePastOrder(order);
    }
}
