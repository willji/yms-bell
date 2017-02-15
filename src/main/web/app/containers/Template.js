/**
 * Created by xuemingli on 2016/10/17.
 */

import React from 'react'
import { connect } from 'react-redux'
import autobind from 'core-decorators/lib/autobind';
import Spin from 'antd/lib/spin';
import Modal from 'antd/lib/modal';
import Button from 'antd/lib/button';
import Form from 'antd/lib/form';
import Input from 'antd/lib/input';
import Card from 'antd/lib/card';
import notification from 'antd/lib/notification';
import message from 'antd/lib/message';
import Codemirror from 'react-codemirror';
import * as constants from '../constants';
import * as TemplateActions from '../actions/template';
import Divider from '../component/Divider';
import {set as navigate} from '../actions/navigation';

import 'codemirror/lib/codemirror.css';
import 'codemirror/mode/groovy/groovy.js';
import 'codemirror/theme/material.css';
import '../styles/codemirror.css';


const formItemLayout = {
  labelCol: { span: 2 },
  wrapperCol: { span: 20 },
};

const options = {
  lineNumbers: true,
  mode: 'groovy',
  theme: 'material',
  fixedGutter: true,
  indentUnit: 4
};

const FormItem = Form.Item;


@connect(state => ({
  template: state.template,
  routing: state.routing
}), {
  navigate,
  get: TemplateActions.get,
  create: TemplateActions.create,
  update: TemplateActions.update
})
@Form.create()
export default class Template extends React.Component {
  constructor(props) {
    super(props);
    const {id} = this.props.params;
    this.props.get(id);
    this.state = {
      template: {name: '', script: '', items: [], creator: {name: ''}},
      loading: false,
      visible: false,
      currentId: id
    }
  }

  @autobind
  showModal(template = null) {
    if (!template) {
      template = {name: '', script: constants.SCRIPT_TEMPLATE};
      this.setState({currentId: -1});
    } else {
      this.setState({currentId: template.id});
    }
    this.props.form.resetFields();
    this.props.form.setFieldsValue({name: template.name});
    this.setState({visible: true, code: template.script});
  }

  @autobind
  handleSubmit() {
    if (this.state.loading) {
      message.warning("请勿重复提交");
      return
    }
    this.setState({loading: true});
    const {name} = this.props.form.getFieldsValue();
    if (this.state.currentId < 0) {
      this.props.create({ name, script: this.state.code })
    } else {
      this.props.update(this.state.currentId, {name, script: this.state.code})
    }
  }

  @autobind
  handleCancel() {
    if (this.state.loading) {
      message.warning("正在提交中...");
      return
    }
    this.setState({visible: false});
  }

  componentWillReceiveProps(props) {
    if (props.template.id != this.props.template.id) {
      switch (props.template.status) {
        case constants.STATUS_REQUEST:
          this.setState({loading: true});
          break;
        case constants.STATUS_FAILURE:
          this.setState({loading: false});
          notification.error({message: '错误', description: props.template.error});
          break;
        case constants.STATUS_SUCCESS:
          this.props.navigate('template', [{name: '模板列表', url: '/templates'}, {name: props.template.res.name}]);
          this.setState({loading: false, template: props.template.res, visible: false});
          break;
      }
    }
    this.props = props;
  }

  renderItem(item){
    return (
      <Card title={item.name} key={item.id} extra={<a href={`${constants.URL_PREFIX}/item/${item.id}`}>查看</a>} style={{maxWidth: 300}}>
        <p>{item.descriptor}</p>
      </Card>
    )
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <div>
        <Spin spinning={this.state.loading}>
          <Card title={this.state.template.name} extra={<Button onClick={() => this.showModal(this.state.template)} icon="edit" type="primary"/>}>
            <Codemirror options={Object.assign({}, options, {readOnly: true})} value={this.state.template.script}/>
            <Divider content="相关监控项"/>
            {this.state.template.items.map(item => this.renderItem(item))}
          </Card>
        </Spin>
        <Modal title="编辑模板"
               width="80%"
               visible={this.state.visible}
               onOk={this.handleSubmit}
               onCancel={this.handleCancel}>
          <Form horizontal onSubmit={this.handleSubmit}>
            <FormItem {...formItemLayout} label="名称">
              {getFieldDecorator('name', { initialValue: '' })(
                <Input type="text" placeholder="请输入模板名称" />
              )}
            </FormItem>
            <FormItem {...formItemLayout} label="代码">
              <Codemirror options={options} value={this.state.code} onChange={value => this.setState({code: value})}/>
            </FormItem>
          </Form>
        </Modal>
      </div>
    )
  }
}
