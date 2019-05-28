package com.hong.weixin.menu.repository;

import com.hong.weixin.menu.domain.SelfMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SelfMenuRepository extends JpaRepository<SelfMenu, String> {

}
