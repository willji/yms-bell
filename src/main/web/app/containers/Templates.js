/**
 * Created by xuemingli on 2016/10/13.
 */
import React from 'react'
import { connect } from 'react-redux'
import history from 'react-router/lib/browserHistory'
import autobind from 'core-decorators/lib/autobind';
import Spin from 'antd/lib/spin';
import Table from 'antd/lib/table';
import Tabs from 'antd/lib/tabs';
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
const TabPane = Tabs.TabPane;


@connect(state =>({
  templates: state.templates,
  template: state.template,
  routing: state.routing
}), {
  navigate,
  list: TemplateActions.list,
  create: TemplateActions.create,
  update: TemplateActions.update,
  delete: TemplateActions.remove
})
@Form.create()
export default class Templates extends React.Component {
  constructor(props) {
    super(props);
    this.props.navigate('template', [{name: '模板列表'}]);
    const {page = 1, size = 50} = this.props.location.query;
    this.props.list(page, size);
    this.state = {
      templates: {list:[], current: 1, size: 50, total:0},
      loading: true,
      visible: false,
      code: constants.SCRIPT_TEMPLATE,
      currentId: -1
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
    const {page = 1, size = 50} = props.location.query;
    if (parseInt(page) !== this.state.templates.current || parseInt(size) !== this.state.templates.size) {
      this.props.list(page, size)
    }

    if (this.props.templates.id !== props.templates.id) {
      if (props.templates.status === constants.STATUS_REQUEST) {
        this.setState({loading: true})
      }
      if (props.templates.status === constants.STATUS_SUCCESS) {
        this.setState({
          templates: props.templates.res,
          loading: false})
      }
      if (props.templates.status === constants.STATUS_FAILURE) {
        notification.error({message: '错误', description: props.templates.error});
      }
    }

    if (this.props.template.id !== props.template.id) {
      if (props.template.status === constants.STATUS_REQUEST) {
        this.setState({loading: true});
      }
      if (props.template.status === constants.STATUS_SUCCESS) {
        this.setState({loading: false, visible: false});
        if (this.state.visible) {
          this.props.list(this.state.templates.page, this.state.templates.size);
        }
      }
      if (props.template.status === constants.STATUS_FAILURE) {
        notification.error({message: '错误', description: props.template.error});
        this.setState({loading: false, visible: false});
      }
    }

    this.props = props;
  }

  @autobind
  renderExpend(template) {
    const renderItem = item => {
      return (
        <Card title={item.name} key={`item.id`} extra={<a href={`/item/${item.id}`}>查看</a>} style={{maxWidth: 300}}>
          <p>{item.descriptor}</p>
        </Card>
      )
    };
    return (
      <Tabs defaultActiveKey="1">
        <TabPane tab="代码" key="1">
          <Codemirror options={Object.assign({}, options, {readOnly: true})} value={template.script}/>
        </TabPane>
        <TabPane tab="关联项" key="2">
          {template.items.map(item => renderItem(item))}
        </TabPane>
      </Tabs>
    )
  }

  render() {
    const columns = [
      {
        title: '名称',
        dataIndex: 'name',
        key: 'name',
        render: (t, r)=> <a href={`${constants.URL_PREFIX}/template/${r.id}`}>{t}</a>
      },
      {
        title: '创建者',
        dataIndex: 'creator.name',
        key: 'creator'
      },
      {
        title: '创建时间',
        dataIndex: 'timestamp',
        key: 'timestamp',
        render: t => <span>{new Date(t).toLocaleString()}</span>
      },
      {
        title: "修改",
        key: 'update',
        render: (t, r) => <Button onClick={() => this.showModal(r)} icon="edit" />
      },
      {
        title: "删除",
        key: 'delete',
        render: (t, r) => <Button onClick={() => this.props.delete(r.id)} disabled={r.items.length > 0} icon="delete" />
      }
    ];

    const pagination = {
      total: this.state.templates.total,
      current: this.state.templates.current,
      pageSize: this.state.templates.size,
      showSizeChanger: true,
      onShowSizeChange: (current, pageSize) => {
        history.push(`${constants.URL_PREFIX}/templates?page=${current}&size=${pageSize}`)
      },
      onChange: (current) => {
        history.push(`${constants.URL_PREFIX}/templates?page=${current}&size=${this.state.templates.size}`)
      },
    };

    const { getFieldDecorator } = this.props.form;

    return (
      <div>
        <Spin spinning={this.state.loading}>
          <div>
            <Button type="primary" onClick={() => this.showModal()} >创建</Button>
          </div>
          <div style={{marginTop: 8}}>
            <Table columns={columns}
                   rowKey="id"
                   dataSource={this.state.templates.list}
                   pagination={pagination}
                   expandedRowRender={this.renderExpend}/>
          </div>
          <Modal title="创建新模板"
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
        </Spin>
      </div>
    )
  }
}
