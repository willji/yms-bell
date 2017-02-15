/**
 * Created by xuemingli on 16/9/13.
 */
import React from 'react'
import { connect } from 'react-redux'
import autobind from 'core-decorators/lib/autobind';
import Spin from 'antd/lib/spin';
import Modal from 'antd/lib/modal';
import Button from 'antd/lib/button';
import Form from 'antd/lib/form';
import Input from 'antd/lib/input';
import AutoComplete from 'antd/lib/auto-complete';
import notification from 'antd/lib/notification';
import message from 'antd/lib/message';
import Card from 'antd/lib/card';
import * as constants from '../constants';
import * as ItemAction from '../actions/item';
import * as TemplateAction from '../actions/template';
import {set as navigate} from '../actions/navigation';
import ItemDetail from './ItemDetail';


const FormItem = Form.Item;

const formItemLayout = {
  labelCol: { span: 2 },
  wrapperCol: { span: 20 },
};


@connect(state => ({
  item: state.item,
  templateSearchResult: state.templateSearchResult,
  vars: state.vars,
  itemVars: state.itemVars
}), {
  navigate,
  get: ItemAction.get,
  enable: ItemAction.enable,
  disable: ItemAction.disable,
  create: ItemAction.create,
  update: ItemAction.update,
  searchTemplate: TemplateAction.search,
  getVars: TemplateAction.getVars,
  getItemVars: ItemAction.getVars
})
@Form.create()
export default class Profile extends React.Component {
  constructor(props) {
    super(props);
    const {id} = this.props.params;
    this.props.get(id);
    this.state = {
      loading: false,
      visible: false,
      item: {name:'', cron:'', descriptor: '', templateName: '', enable: true},
      templates: [],
      vars: {},
      currentId: -1,
      updateId: null,
    }
  }

  @autobind
  showModal(copy = false) {
    this.props.getItemVars(this.state.item.id);
    this.props.form.resetFields();
    this.props.form.setFieldsValue({name: this.state.item.name, cron: this.state.item.cron, descriptor: this.state.item.descriptor, template: this.state.item.templateName});
    this.setState({currentId: this.state.item.id, enable: this.state.item.enable});
    if (copy) {
      this.setState({currentId: -1, enable: true})
    }
    this.setState({visible: true});
  }

  @autobind
  handleChangeStatus() {
    if (!this.state.enable) {
      this.props.enable(this.state.item.id);
    } else {
      this.props.disable(this.state.item.id);
    }
  }

  @autobind
  handleSubmit() {
    if (this.state.loading) {
      message.warning("请勿重复提交");
      return
    }
    this.setState({loading: true});
    const {name, cron, template, vars, descriptor} = this.props.form.getFieldsValue();
    const variables = {};
    Object.keys(vars).forEach(k => {
      const v = this.state.vars[k];
      variables[k] = vars[k];
      if (v.type === 'INT') {
        variables[k] = parseInt(vars[k])
      } else if (v.type === 'FLOAT') {
        variables[k] = parseFloat(vars[k])
      }
    });
    if (this.state.currentId < 0) {
      this.props.create(name, cron, template, JSON.stringify(variables), descriptor)
    } else  {
      this.props.update(this.state.currentId, name, cron, template, this.state.enable, JSON.stringify(variables), descriptor)
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

  @autobind
  handleSearchTemplate(value) {
    this.props.searchTemplate(value);
  }

  @autobind
  handleTemplateSelect(value) {
    this.props.getVars(value);

  }

  componentWillReceiveProps(props) {
    if (props.item.id !== this.props.item.id) {
      switch (props.item.status) {
        case constants.STATUS_REQUEST:
          this.setState({loading: true});
          break;
        case  constants.STATUS_FAILURE:
          notification.error({message: "错误", description: props.item.error});
          this.setState({loading: false});
          break;
        case constants.STATUS_SUCCESS:
          this.props.navigate('item', [{name: '监控项列表', url: '/items'}, {name: props.item.res.name}]);
          this.setState({loading: false, visible: false, item: props.item.res, updateId: props.item.id});
          break;
      }
    }

    if (props.templateSearchResult.id !== this.props.templateSearchResult.id) {
      switch (props.templateSearchResult.status) {
        case constants.STATUS_REQUEST:
          this.setState({loading: true});
          break;
        case  constants.STATUS_FAILURE:
          notification.error({message: "错误", description: props.templateSearchResult.error});
          this.setState({loading: false});
          break;
        case constants.STATUS_SUCCESS:
          this.setState({loading: false, templates: props.templateSearchResult.res});
          break;
      }
    }

    if (props.vars.id !== this.props.vars.id) {
      switch (props.vars.status) {
        case constants.STATUS_REQUEST:
          this.setState({loading: true});
          break;
        case  constants.STATUS_FAILURE:
          notification.error({message: "错误", description: props.vars.error});
          this.setState({loading: false});
          break;
        case constants.STATUS_SUCCESS:
          this.setState({loading: false, vars: props.vars.res});
          break;
      }
    }

    if (props.itemVars.id !== this.props.itemVars.id) {
      switch (props.itemVars.status) {
        case constants.STATUS_REQUEST:
          this.setState({loading: true});
          break;
        case  constants.STATUS_FAILURE:
          notification.error({message: "错误", description: props.itemVars.error});
          this.setState({loading: false});
          break;
        case constants.STATUS_SUCCESS:
          this.setState({loading: false, vars: props.itemVars.res});
          break;
      }
    }

    this.props = props;
  }

  @autobind
  renderFormItem(v) {
    const { getFieldDecorator } = this.props.form;
    return (
      <FormItem {...formItemLayout} key={v.name} label={v.display}>
        {getFieldDecorator(`vars.${v.name}`, {initialValue: v.value})(
          <Input type="text" placeholder={v.desc}/>
        )}
      </FormItem>
    )
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const extra = (
        <Button onClick={() => this.showModal()} icon="edit" type="primary"/>
    );

    return (
      <div>
        <Spin spinning={this.state.loading}>
          <Card title={this.state.item.name} extra={extra}>
            <ItemDetail item={this.state.item} updateId={this.state.updateId}/>
          </Card>
        </Spin>
        <Modal title="创建新监控项"
               width="80%"
               visible={this.state.visible}
               onOk={this.handleSubmit}
               onCancel={this.handleCancel}>
          <Form horizontal onSubmit={this.handleSubmit}>
            <FormItem {...formItemLayout} label="名称">
              {getFieldDecorator('name', { initialValue: '' })(
                <Input type="text" placeholder="请输入监控项名称" />
              )}
            </FormItem>
            <FormItem {...formItemLayout} label="执行计划">
              {getFieldDecorator('cron', { initialValue: '0 */3 * * * ?' })(
                <Input type="text" placeholder="请输入cron表达式" />
              )}
            </FormItem>
            <FormItem {...formItemLayout} label="模板">
              {getFieldDecorator('template', {})(
                <AutoComplete onChange={this.handleSearchTemplate} onSelect={this.handleTemplateSelect}>
                  {this.state.templates.map(t => <Option key={t.id} value={t.name}>{t.name}</Option>)}
                </AutoComplete>
              )}
            </FormItem>
            {Object.keys(this.state.vars).map(k => this.renderFormItem(this.state.vars[k]))}
            <FormItem {...formItemLayout} label="说明">
              {getFieldDecorator('descriptor', { initialValue: '' })(
                <Input type="textarea" placeholder="说明" />
              )}
            </FormItem>
          </Form>
        </Modal>
      </div>
    )
  }
}
