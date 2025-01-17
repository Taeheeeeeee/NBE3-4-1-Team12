package com.ll.coffeeBean.global.initData;

import com.ll.coffeeBean.domain.coffeeBean.entity.CoffeeBean;
import com.ll.coffeeBean.domain.coffeeBean.repository.CoffeeBeanRepository;
import com.ll.coffeeBean.domain.coffeeBean.service.CoffeeBeanService;
import com.ll.coffeeBean.domain.order.entity.DetailOrder;
import com.ll.coffeeBean.domain.order.entity.MenuOrder;
import com.ll.coffeeBean.domain.order.repository.DetailOrderRepository;
import com.ll.coffeeBean.domain.order.repository.OrderRepository;
import com.ll.coffeeBean.domain.order.service.OrderService;
import com.ll.coffeeBean.domain.siteUser.entity.SiteUser;
import com.ll.coffeeBean.domain.siteUser.repository.UserRepository;
import com.ll.coffeeBean.domain.siteUser.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {
	private final OrderService orderService;
	private final OrderRepository orderRepository;
	private final UserService userService;
	private final UserRepository userRepository;
	private final DetailOrderRepository detailOrderRepository;
	private final CoffeeBeanService coffeeBeanService;
	private final CoffeeBeanRepository coffeeBeanRepository;

	@Autowired
	@Lazy
	private BaseInitData self;

	@Bean
	public ApplicationRunner baseInitDataApplicationRunner() {
		return args -> {
			self.work1();
		};
	}

	@Transactional
	public void work1() {
		if(coffeeBeanService.count() == 0) {
			CoffeeBean bean1 = new CoffeeBean("bean1", 1000, 49);
			coffeeBeanRepository.save(bean1);
			CoffeeBean bean2 = new CoffeeBean("bean2", 1000, 48);
			coffeeBeanRepository.save(bean2);
			CoffeeBean bean3 = new CoffeeBean("bean3", 1000, 47);
			coffeeBeanRepository.save(bean3);
		}

		SiteUser user1 = new SiteUser();
		if(userService.count() == 0) {
			user1.setEmail("user1@naver.com");
			userRepository.save(user1);
		} else {
			user1 = userService.findByEmail("user1@naver.com");
		}

		if (orderService.count() == 0) {
			MenuOrder order1 = new MenuOrder();
			order1.setCustomer(user1);

			DetailOrder bean1 = new DetailOrder("bean1", 1, 1000, order1);
			detailOrderRepository.save(bean1);
			DetailOrder bean2 = new DetailOrder("bean2", 2, 1000, order1);
			detailOrderRepository.save(bean2);
			DetailOrder bean3 = new DetailOrder("bean3", 3, 1000, order1);
			detailOrderRepository.save(bean3);

			List<DetailOrder> orderList = new ArrayList<>();
			orderList.add(bean1);
			orderList.add(bean2);
			orderList.add(bean3);
			order1.setOrders(orderList);

			orderRepository.save(order1);
		}
	}
}
