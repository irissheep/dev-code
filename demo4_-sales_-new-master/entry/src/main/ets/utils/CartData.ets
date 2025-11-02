// CartData.ets：购物车数据管理工具
import { AppStorage } from '@harmonyos.app.ability.AppStorage';

// 定义商品类型（和你项目中的商品数据结构一致）
export interface CartItem {
  id: string; // 商品唯一ID（必填，避免重复添加）
  name: string; // 商品名称
  price: number; // 单价
  count: number; // 数量
  image: string; // 图片路径（可选）
}

// 初始化购物车数据（如果AppStorage中没有，就设为空数组）
if (!AppStorage.Has('cartList')) {
  AppStorage.SetOrCreate('cartList', [] as CartItem[]);
}

// 1. 添加商品到购物车（如果已存在，就增加数量）
export function addToCart(newItem: CartItem) {
  const cartList = AppStorage.Get<CartItem[]>('cartList') || [];
  // 查找商品是否已在购物车中
  const existingItem = cartList.find(item => item.id === newItem.id);
  if (existingItem) {
    existingItem.count += newItem.count; // 已存在，数量+1
  } else {
    cartList.push({ ...newItem, count: 1 }); // 不存在，新增商品
  }
  AppStorage.Set('cartList', cartList); // 更新购物车数据
  return true;
}

// 2. 从购物车删除商品
export function removeFromCart(itemId: string) {
  let cartList = AppStorage.Get<CartItem[]>('cartList') || [];
  cartList = cartList.filter(item => item.id !== itemId); // 过滤掉要删除的商品
  AppStorage.Set('cartList', cartList);
}

// 3. 更新商品数量（增减）
export function updateCartItemCount(itemId: string, newCount: number) {
  if (newCount < 1) return; // 数量不能小于1
  const cartList = AppStorage.Get<CartItem[]>('cartList') || [];
  const item = cartList.find(item => item.id === itemId);
  if (item) {
    item.count = newCount;
    AppStorage.Set('cartList', cartList);
  }
}

// 4. 计算购物车总金额
export function getCartTotal() {
  const cartList = AppStorage.Get<CartItem[]>('cartList') || [];
  return cartList.reduce((total, item) => total + (item.price * item.count), 0);
}