/**
 * Created by xuemingli on 16/9/12.
 */
import React from 'react'
import { connect } from 'react-redux'
import autobind from 'core-decorators/lib/autobind';
import history from 'react-router/lib/browserHistory'
import Modal from 'antd/lib/modal';
import Button from 'antd/lib/button';
import Form from 'antd/lib/form';
import Input from 'antd/lib/input';
import message from 'antd/lib/message';
import Store from '@comynli/store';
import * as constants from '../constants';
import * as AuthSettings from '../authentication/settings';
import * as AccountAction from '../actions/account';


const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 14 },
};

const FormItem = Form.Item;

@connect(state => {
  return {account: state.account, routing: state.routing}
}, {login: AccountAction.login})
@Form.create()
export default class Login extends React.Component {
  constructor(props) {
    super(props);
    this.state = {loading: false}
  }


  @autobind
  handleSubmit() {
    this.setState({loading: true});
    const {username, password} = this.props.form.getFieldsValue();
    this.props.login(username, password);
  }

  componentWillReceiveProps(props) {
    this.props = props;
    if (this.props.account.status === constants.STATUS_SUCCESS) {
      Store.set(AuthSettings.TOKEN_KEY, this.props.account.res, 0, new Date(this.props.account.res.expiration));
      history.goBack();
    }
    if (this.props.account.status === constants.STATUS_FAILURE) {
      message.error(this.props.account.error)
    }
    this.setState({loading: false})
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    return(
      <Modal title="登录"
             visible={true}
             onOk={this.handleSubmit}
             footer={[
               <Button key="submit" type="primary" size="large" loading={this.state.loading} onClick={this.handleSubmit}>
                 登录
               </Button>,
             ]}
      >
        <Form horizontal onSubmit={this.handleSubmit}>
          <FormItem {...formItemLayout} label="用户名">
            {getFieldDecorator('username', { initialValue: '' })(
              <Input type="text" placeholder="请输入用户名" />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="密码">
            {getFieldDecorator('password', { initialValue: '' })(
              <Input type="password" placeholder="请输入密码" />
            )}
          </FormItem>
        </Form>
      </Modal>
    )
  }
}
