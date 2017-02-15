import { combineReducers } from 'redux';
import * as utils from './utils';
import auth from '../authentication/reducer';
import navigation from './navigation';
import * as AccountAction from '../actions/account';
import * as ItemAction from '../actions/item';
import * as TemplateAction from '../actions/template';
import * as AppAction from '../actions/app';

const account = utils.createReduce(AccountAction.LOGIN_TYPE_SET);
const subscriptions = utils.createReduce(AccountAction.GET_MY_SUBSCRIPTIONS_TYPE_SET, ItemAction.SUBSCRIBE_ITEM_TYPE_SET, ItemAction.UNSUBSCRIBE_ITEM_TYPE_SET);
const items = utils.createReduce(ItemAction.LIST_ALL_ITEMS_TYPE_SET,);
const item = utils.createReduce(
  ItemAction.CREATE_ITEM_TYPE_SET,
  ItemAction.GET_ITEM_INFO_TYPE_SET,
  ItemAction.UPDATE_ITEM_TYPE_SET,
  ItemAction.ENABLE_ITEM_TYPE_SET,
  ItemAction.DISABLE_ITEM_TYPE_SET,
  ItemAction.ADD_API_TYPE_SET,
  ItemAction.REMOVE_API_TYPE_SET,
  ItemAction.ADD_APP_TYPE_SET,
  ItemAction.REMOVE_APP_TYPE_SET
);
const empty = utils.createReduce(ItemAction.EXECUTE_ITEM_TYPE_SET);
const templates = utils.createReduce(TemplateAction.LIST_ALL_TEMPLATES_TYPE_SET);
const template = utils.createReduce(
  TemplateAction.CREATE_TEMPLATE_TYPE_SET,
  TemplateAction.UPDATE_TEMPLATE_TYPE_SET,
  TemplateAction.DELETE_TEMPLATE_TYPE_SET,
  TemplateAction.GET_TEMPLATE_INFO_TYPE_SET
);

const vars = utils.createReduce(TemplateAction.GET_TEMPLATE_VARS_TYPE_SET);
const templateSearchResult = utils.createReduce(TemplateAction.SEARCH_TEMPLATE_TYPE_SET);
const appSearchResult = utils.createReduce(AppAction.APP_SEARCH_TYPE_SET);
const apiSearchResult = utils.createReduce(AppAction.API_SEARCH_TYPE_SET);

const itemApps = utils.createReduce(
  ItemAction.GET_ITEM_APPS_TYPE_SET
);
const itemApis = utils.createReduce(
  ItemAction.GET_ITEM_APIS_TYPE_SET,

);
const itemVars = utils.createReduce(ItemAction.GET_ITEM_VARS_TYPE_SET);
const app = utils.createReduce(AppAction.GET_APP_TYPE_SET);
const api = utils.createReduce(AppAction.GET_API_TYPE_SET, AppAction.ADD_ITEM_TYPE_SET, AppAction.REMOVE_ITEM_TYPE_SET);
const itemSearchResult = utils.createReduce(ItemAction.SEARCH_ITEM_TYPE_SET);

const rootReducer = combineReducers({
  auth,
  navigation,
  account,
  subscriptions,
  items,
  item,
  empty,
  templates,
  template,
  vars,
  templateSearchResult,
  appSearchResult,
  apiSearchResult,
  itemApps,
  itemApis,
  itemVars,
  app,
  api,
  itemSearchResult
});

export default rootReducer;
