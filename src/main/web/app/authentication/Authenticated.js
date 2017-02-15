/**
 * Created by xuemingli on 16/5/25.
 */
import React from 'react';
import {connect} from 'react-redux';
import autobind from 'core-decorators/lib/autobind'
import Store from '@comynli/store';
import Button from 'antd/lib/button';
import notification from 'antd/lib/notification';
import history from 'react-router/lib/browserHistory'
import * as settings from './settings';
import * as constants from '../constants';

const key = "Authenticated";


@connect(state => state)
export default class Authenticated extends React.Component {
  constructor(props) {
    super(props);
    this.loginUrl = settings.LOGIN_URL;
    if (typeof this.loginUrl === 'function') {
      this.loginUrl = this.loginUrl();
    }
    this.loginUrl = `${constants.URL_PREFIX}${this.loginUrl}`;
    this.btn = <Button type="primary" size="small" onClick={this.redirect}>去登录</Button>;

  }

  @autobind
  redirect() {
    notification.close(key);
    history.push(this.loginUrl);
  }

  @autobind
  checkAuth() {
    const token = Store.get(settings.TOKEN_KEY || 'token');
    if (!token) {
      notification.error({
        message: settings.UNAUTHORIZED_MESSAGE,
        description: '',
        key,
        btn: this.btn,
        onClose: () => history.push(this.loginUrl)
      });
    }
  }

  @autobind
  checkPermissions(props) {
    if (props.auth.forbidden) {
      Store.remove(settings.TOKEN_KEY || 'token');
      notification.error({
        message: settings.FORBIDDEN_MESSAGE,
        description: '',
        key,
        btn: this.btn,
        onClose: () => history.push(this.loginUrl)
      });
    }
  }

  componentWillReceiveProps(props) {
    this.checkAuth();
    this.checkPermissions(props);
    this.props = props;

  }

  render() {
    return (
      <div>
        {this.props.children}
      </div>
    );
  }
}
