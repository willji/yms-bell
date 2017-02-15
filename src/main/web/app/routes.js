import React from 'react';
import { Route, IndexRoute } from 'react-router';
import * as constants from './constants';
import App from './containers/App';
import Authenticated from './authentication/Authenticated';
import Login from './containers/Login';
import Test from './containers/Test';
import Templates from './containers/Templates';
import Template from './containers/Template';
import Items from './containers/Items';
import Item from './containers/Item';
import ApplicationSearch from './containers/ApplicationSearch';
import Application from './containers/Application';

export default (
  <Route path={constants.URL_PREFIX}>
    <Route component={App}>
      <Route component={Login} path="login"/>
      <Route component={Authenticated}>
        <IndexRoute component={Items}/>
        <Route component={Templates} path="templates"/>
        <Route component={Template} path="template/:id"/>
        <Route component={Items} path="items"/>
        <Route component={Item} path="item/:id"/>
        <Route component={ApplicationSearch} path="apps"/>
        <Route component={Application} path="app/:app"/>
      </Route>
    </Route>
    <Route path="*" component={Test}/>
  </Route>
);
