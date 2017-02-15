/**
 * Created by xuemingli on 16/9/13.
 */
import React from 'react'
import { connect } from 'react-redux'
import autobind from 'core-decorators/lib/autobind';
import history from 'react-router/lib/browserHistory'
import Table from 'antd/lib/table';
import Checkbox from 'antd/lib/checkbox';
import Spin from 'antd/lib/spin';
import Modal from 'antd/lib/modal';
import Button from 'antd/lib/button';
import Form from 'antd/lib/form';
import Input from 'antd/lib/input';
import AutoComplete from 'antd/lib/auto-complete';
import notification from 'antd/lib/notification';
import message from 'antd/lib/message';
import * as constants from '../constants';
import * as ItemAction from '../actions/item';
import * as TemplateAction from '../actions/template';
import * as AppAction from '../actions/app';
import {set as navigate} from '../actions/navigation';
import ItemDetail from './ItemDetail';


const FormItem = Form.Item;

const formItemLayout = {
  labelCol: { span: 2 },
  wrapperCol: { span: 20 },
};


@connect(state => ({
  items: state.items,
  item: state.item,
  templateSearchResult: state.templateSearchResult,
  vars: state.vars,
  itemVars: state.itemVars,
  appSearchResult: state.appSearchResult
}), {
  navigate,
  list: ItemAction.list,
  enable: ItemAction.enable,
  disable: ItemAction.disable,
  create: ItemAction.create,
  update: ItemAction.update,
  getItemVars: ItemAction.getVars,
  searchTemplate: TemplateAction.search,
  getVars: TemplateAction.getVars,
  searchApp: AppAction.searchApp
})
@Form.create()
export default class Profile extends React.Component {
  constructor(props) {
    super(props);
    this.props.navigate('item', [{name: '监控项列表'}]);
    const {page = 1, size = 50} = this.props.location.query;
    this.props.list(page, size);
    this.state = {
      loading: false,
      visible: false,
      items:{list: [], current: 1, size: 50},
      templates: [],
      vars: {},
      currentId: -1,
      enable: true,
      appSearchResult: []
    }
  }

  @autobind
  showModal(item = null, copy = false) {
    this.props.form.resetFields();
    if (item) {
      this.props.getItemVars(item.id);
      this.props.form.setFieldsValue({name: item.name, cron: item.cron,
        descriptor: item.descriptor, template: item.templateName});
      this.setState({currentId: item.id, enable: item.enable});
      if (copy) {
        this.setState({currentId: -1, enable: true})
      }
    }
    this.setState({visible: true});
  }

  @autobind
  handleChangeStatus(id, status) {
    if (status) {
      this.props.enable(id);
    } else {
      this.props.disable(id);
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
    const {page = 1, size = 50} = props.location.query;
    if (parseInt(page) !== this.state.items.current || parseInt(size) !== this.state.items.size) {
      this.props.list(page, size)
    }

    if (props.items.id !== this.props.items.id) {
      switch (props.items.status) {
        case constants.STATUS_REQUEST:
          this.setState({loading: true});
          break;
        case constants.STATUS_FAILURE:
          notification.error({message: '错误', description: props.items.error});
          this.setState({loading: false});
          break;
        case constants.STATUS_SUCCESS:
          this.setState({loading: false, visible: false, items: props.items.res});
          break;
      }
    }

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
          this.setState({loading: false});
          this.props.list(this.state.items.current, this.state.items.size);
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
  renderExpend(item) {
    return <ItemDetail item={item}/>;
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

    const columns = [
      {
        title: '启用',
        dataIndex: 'enable',
        key: 'status',
        render: (t, r) => <Checkbox checked={t} onChange={e => this.handleChangeStatus(r.id, e.target.checked)}/>
      },
      {
        title: '名称',
        dataIndex: 'name',
        key: 'name',
        render: (t, r) => <a href={`${constants.URL_PREFIX}/item/${r.id}`}>{t}</a>
      },
      {
        title: '模板',
        dataIndex: 'templateName',
        key: 'template',
        render: (t, r) => <a href={`${constants.URL_PREFIX}/template/${r.templateId}`}>{t}</a>
      },
      {
        title: '执行计划',
        dataIndex: 'cron',
        key: 'corn'
      },
      {
        title: '创建者',
        dataIndex: 'creator.name',
        key: 'creator'
      },
      {
        title: '最后修改时间',
        dataIndex: 'lastModifiedTime',
        key: 'lastModifiedTime',
        render: t => <span>{(new Date(t)).toLocaleString()}</span>
      },
      {
        title: '更新',
        key: 'update',
        render: (t, r) => <Button icon="edit" onClick={() => this.showModal(r)}/>
      },
      {
        title: '复制',
        key: 'copy',
        render: (t, r) => <Button icon="copy" onClick={() => this.showModal(r, true)}/>
      }
    ];

    const pagination = {
      total: this.state.items.total,
      current: this.state.items.current,
      pageSize: this.state.items.size,
      showSizeChanger: true,
      onShowSizeChange: (current, pageSize) => {
        history.push(`${constants.URL_PREFIX}/items?page=${current}&size=${pageSize}`)
      },
      onChange: (current) => {
        history.push(`${constants.URL_PREFIX}/items?page=${current}&size=${this.state.items.size}`)
      },
    };

    return (
      <div>
        <Spin spinning={this.state.loading}>
          <div>
            <Button type="primary" onClick={() => this.showModal()} >创建</Button>
          </div>
          <Table rowKey="id"
                 columns={columns}
                 dataSource={this.state.items.list}
                 pagination={pagination}/>
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
