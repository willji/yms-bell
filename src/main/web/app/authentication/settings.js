/**
 * Created by xuemingli on 16/5/25.
 */

export const UNAUTHORIZED_MESSAGE = '未认证用户';
export const FORBIDDEN_MESSAGE = '未授权的访问';
export const ENABLE_AUTH = true;
export const TOKEN_KEY = '_token';
export const TOKEN_HEADER = 'X-Authorization-Token';
export const LOGIN_URL = '/login';
export const FORBIDDEN_ACTION = {
  label: '重新登录',
  redirect: '/login'
};
