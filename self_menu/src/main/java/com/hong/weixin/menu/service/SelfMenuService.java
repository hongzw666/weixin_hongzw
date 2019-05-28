package com.hong.weixin.menu.service;

import com.hong.weixin.menu.domain.SelfMenu;

public interface SelfMenuService {

	SelfMenu getMenu();

	void saveMenu(SelfMenu selfMenu);

}
