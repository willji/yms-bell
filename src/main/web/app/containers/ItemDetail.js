/**
 * Created by xuemingli on 2016/10/24.
 */
/**
 * Created by xuemingli on 16/9/13.
 */
import React from 'react'
import { connect } from 'react-redux'
import autobind from 'core-decorators/lib/autobind';
import Table from 'antd/lib/table';
import Checkbox from 'antd/lib/checkbox';
import AutoComplete from 'antd/lib/auto-complete';
import Tabs from 'antd/lib/tabs';
import Card from 'antd/lib/card';
import Button from 'antd/lib/button';
import Popconfirm from 'antd/lib/popconfirm';
import Pagination from 'antd/lib/pagination';
import notification from 'antd/lib/notification';
import * as constants from '../constants';
import * as ItemAction from '../actions/item';
import * as AppAction from '../actions/app';


const TabPane = Tabs.TabPane;


@connect(state => ({
  vars: state.itemVars,
  apps: state.itemApps,
  apis: state.itemApis,
  appSearchResult: state.appSearchResult,
  apiSearchResult: state.apiSearchResult
}), {
  addApp: ItemAction.addApp,
  removeApp: ItemAction.removeApp,
  addApi: ItemAction.addApi,
  removeApi: ItemAction.removeApi,
  getApps: ItemAction.getApps,
  getApis: ItemAction.getApis,
  getVars: ItemAction.getVars,
  searchApp: AppAction.searchApp,
  searchApi: AppAction.searchApi,
})
export default class Profile extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      appSearchResult: [],
      apiSearchResult: [],
      vars: {},
      apps: {list: [], current: 1, size: 50, total: 0, count: 0},
      apis: {list: [], current: 1, size: 50, total: 0, count: 0},
      currentApp: 0,
      currentUpdate: null,
    }
  }

  @autobind
  handleTabChange(key) {
    if (key === '2') {
      this.props.getVars(this.props.item.id);
      this.setState({currentUpdate: 'vars'})
    }
    if (key === '3') {
      this.props.getApps(this.props.item.id);
      this.setState({currentUpdate: 'apps'})
    }
  }

  @autobind
  handleAddApp(name) {
    this.props.addApp(this.props.item.id, name);
    this.setState({currentUpdate: 'apps'});
  }

  @autobind
  handleRemoveApp(name) {
    this.props.removeApp(this.props.item.id, name);
    this.setState({currentUpdate: 'apps'});
  }

  @autobind
  handleSearchApp(q) {
    this.props.searchApp(q, this.props.item.id);
  }

  @autobind
  handleSearchApi(q, app) {
    this.props.searchApi(q, app, this.props.item.id);
  }

  @autobind
  handleChangeApiSet(id, name, apiSet, direction) {
    if (direction === 'right') {
      this.props.addApi(id, name, apiSet);
    } else {
      this.props.removeApi(id, name, apiSet);
    }
  }

  @autobind
  handleAddApi(name, api) {
    this.props.addApi(this.props.item.id, name, api);
    this.setState({currentUpdate: 'apis'});
  }

  @autobind
  handleRemoveApi(name, api) {
    this.props.removeApi(this.props.item.id, name, api);
    this.setState({currentUpdate: 'apis'});
  }

  @autobind
  handleShowApp(id) {
    this.props.getApis(this.props.item.id, id);
    this.setState({currentApp: id});
  }

  componentWillReceiveProps(props) {
    if (props.appSearchResult.id !== this.props.appSearchResult.id) {
      switch (props.appSearchResult.status) {
        case constants.STATUS_REQUEST:
          this.setState({loading: true});
          break;
        case  constants.STATUS_FAILURE:
          notification.error({message: "错误", description: props.appSearchResult.error});
          this.setState({loading: false});
          break;
        case constants.STATUS_SUCCESS:
          this.setState({loading: false, appSearchResult: props.appSearchResult.res});
          break;
      }
    }

    if (props.apiSearchResult.id !== this.props.apiSearchResult.id) {
      switch (props.apiSearchResult.status) {
        case constants.STATUS_REQUEST:
          this.setState({loading: true});
          break;
        case  constants.STATUS_FAILURE:
          notification.error({message: "错误", description: props.apiSearchResult.error});
          this.setState({loading: false});
          break;
        case constants.STATUS_SUCCESS:
          this.setState({loading: false, apiSearchResult: props.apiSearchResult.res});
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

    if (props.apps.id !== this.props.apps.id) {
      switch (props.apps.status) {
        case constants.STATUS_REQUEST:
          this.setState({loading: true});
          break;
        case  constants.STATUS_FAILURE:
          notification.error({message: "错误", description: props.apps.error});
          this.setState({loading: false});
          break;
        case constants.STATUS_SUCCESS:
          this.setState({loading: false, apps: props.apps.res});
          break;
      }
    }

    if (props.apis.id !== this.props.apis.id) {
      switch (props.apis.status) {
        case constants.STATUS_REQUEST:
          this.setState({loading: true});
          break;
        case  constants.STATUS_FAILURE:
          notification.error({message: "错误", description: props.apis.error});
          this.setState({loading: false});
          break;
        case constants.STATUS_SUCCESS:
          this.setState({loading: false, apis: props.apis.res});
          break;
      }
    }

    if (props.updateId !== this.props.updateId) {
      if (this.state.currentUpdate === 'vars') {
        this.props.getVars(props.item.id);
      }
      if (this.state.currentUpdate === 'apps') {
        this.props.getApps(props.item.id, this.state.apps.current, this.state.apps.size);
      }
      if (this.state.currentUpdate === 'apis') {
        this.props.getApis(props.item.id, this.state.currentApp, this.state.apis.current, this.state.apis.size)
      }
    }
    this.props = props;
  }

  @autobind
  render() {
    const item = this.props.item;

    return (
      <Tabs defaultActiveKey="1" onChange={this.handleTabChange}>
        <TabPane tab="描述" key="1">{item.descriptor}</TabPane>
        <TabPane tab="变量" key="2">{this.renderVars()}</TabPane>
        <TabPane tab="应用组" key="3">{this.renderApps()}</TabPane>
      </Tabs>
    )
  }

  @autobind
  renderVars() {
    const columns = [
      {
        title: '变量名',
        dataIndex: 'name',
        key: 'name'
      },
      {
        title: '显示名',
        dataIndex: 'display',
        key: 'display',
      },
      {
        title: '值',
        dataIndex: 'value',
        key: 'value'
      },
      {
        title: '类型',
        dataIndex: 'type',
        key: 'type'
      },
      {
        title: '必选',
        dataIndex: 'required',
        key: 'required',
        render: t => <Checkbox checked={t}/>
      },
      {
        title: '默认值',
        dataIndex: 'defaultValue',
        key: 'defaultValue'
      },
      {
        title: '说明',
        dataIndex: 'desc',
        key: 'desc'
      }
    ];
    return <Table columns={columns}
                  loading={this.state.loading}
                  dataSource={Object.keys(this.state.vars).map(key => this.state.vars[key])}
                  size="small"
                  pagination={false}
                  bordered/>
  }

  @autobind
  renderApp(app) {
    if (app.id !== this.state.currentApp) {
      return null;
    }
    let pagination = null;
    if (this.state.apis.count > 1) {
      pagination = (
        <Pagination current={this.state.apis.current}
                    total={this.state.apis.total}
                    pageSize={this.state.apis.size}
                    onChange={page => this.props.getApis(this.props.item.id, app.name, page, this.state.apis.size)}
        />
      )
    }

    return (
      <div>
        <AutoComplete style={{width: '100%'}}
                      dataSource={this.state.apiSearchResult}
                      onSelect={v => this.handleAddApi(app.name, v)}
                      onChange={v => this.handleSearchApi(v, app.name)}/>
        {this.state.apis.list.map(it => (
          <Button.Group key={it.id} style={{marginTop: 8, width: '100%', display: 'flex'}}>
            <Button style={{flex: 47, textAlign: 'left'}} type="ghost">{it.name}</Button>
            <Button style={{flex: 1}} icon="delete" onClick={() => this.handleRemoveApi(app.name, it.name)}/>
          </Button.Group>
        ))}
        {pagination}
      </div>
    )
  }

  @autobind
  renderApps() {
    const style = {marginTop: 8};
    const extra = app => (
      <Popconfirm title="删除应用会同时删除所有该应用的接口，是否删除？" onConfirm={() => this.handleRemoveApp(app.name)}>
        <a href="#">删除</a>
      </Popconfirm>
    );

    let pagination = null;
    if (this.state.apps.count > 1) {
      pagination = (
        <Card key="pagination" style={style} bordered={false}>
          <Pagination current={this.state.apps.current}
                      total={this.state.apps.total}
                      pageSize={this.state.apps.size}
                      onChange={page => this.props.getApps(this.props.item.id, page, this.state.apps.size)}
          />
        </Card>
      )
    }

    return (
      <div>
        <Card key="new" title="新增监控应用" style={style}>
          <AutoComplete style={{width: '100%'}}
                        dataSource={this.state.appSearchResult}
                        onSelect={this.handleAddApp}
                        onChange={this.handleSearchApp}/>
        </Card>
        {this.state.apps.list.map(it => (
          <Card extra={extra(it)}
                style={style}
                key={it.id}
                bodyStyle={{display: it.id === this.state.currentApp ? 'block' : 'none'}}
                title={<a onClick={() => this.handleShowApp(it.id)}>{it.name}</a>}>
            {this.renderApp(it)}
          </Card>
        ))}
        {pagination}
      </div>
    )
  }
}
