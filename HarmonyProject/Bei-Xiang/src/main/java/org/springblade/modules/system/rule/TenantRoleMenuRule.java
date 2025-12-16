/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.modules.system.rule;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import org.springblade.common.cache.ParamCache;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.system.entity.Menu;
import org.springblade.modules.system.entity.RoleMenu;
import org.springblade.modules.system.service.IMenuService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.springblade.common.constant.TenantConstant.ACCOUNT_MENU_CODE_KEY;
import static org.springblade.common.constant.TenantConstant.MENU_CODES;

/**
 * 租户角色菜单构建
 *
 * @author Chill
 */
@LiteflowComponent(id = "tenantRoleMenuRule", name = "租户角色菜单构建")
public class TenantRoleMenuRule extends NodeComponent {
	@Override
	public void process() throws Exception {
		// 获取上下文
		TenantContext contextBean = this.getFirstContextBean();
		IMenuService menuService = contextBean.getMenuService();
		// 新建租户对应的角色菜单权限
		LinkedList<Menu> userMenus = new LinkedList<>();
		// 获取参数配置的默认菜单集合，逗号隔开
		List<String> menuCodes = Func.toStrList(ParamCache.getValue(ACCOUNT_MENU_CODE_KEY));
		List<Menu> menus = getMenus(menuService, (!menuCodes.isEmpty() ? menuCodes : MENU_CODES), userMenus);
		List<RoleMenu> roleMenuList = new ArrayList<>();
		menus.forEach(menu -> {
			RoleMenu roleMenu = new RoleMenu();
			roleMenu.setMenuId(menu.getId());
			roleMenuList.add(roleMenu);
		});
		// 设置上下文
		contextBean.setRoleMenuList(roleMenuList);
	}

	private List<Menu> getMenus(IMenuService menuService, List<String> codes, LinkedList<Menu> menus) {
		codes.forEach(code -> {
			Menu menu = menuService.getOne(Wrappers.<Menu>query().lambda().eq(Menu::getCode, code).eq(Menu::getIsDeleted, BladeConstant.DB_NOT_DELETED));
			if (menu != null) {
				menus.add(menu);
				recursionMenu(menuService, menu.getId(), menus);
			}
		});
		return menus;
	}

	private void recursionMenu(IMenuService menuService, Long parentId, LinkedList<Menu> menus) {
		List<Menu> menuList = menuService.list(Wrappers.<Menu>query().lambda().eq(Menu::getParentId, parentId).eq(Menu::getIsDeleted, BladeConstant.DB_NOT_DELETED));
		menus.addAll(menuList);
		menuList.forEach(menu -> recursionMenu(menuService, menu.getId(), menus));
	}
}
