import React from 'react';
import {connect} from 'react-redux';
import Menu from 'antd/lib/menu';
import Breadcrumb from 'antd/lib/breadcrumb';
import {URL_PREFIX} from '../constants';

import 'antd/dist/antd.less';
import '../styles/layout.css';

@connect(state => ({navigation: state.navigation}))
export default class App extends React.Component {
  constructor(props) {
    super(props);
  }

  renderBreadcrumbItem(item, key) {
    const content = item.url ? <a href={`${URL_PREFIX}${item.url}`}>{item.name}</a> : <span>{item.name}</span>;
    return <Breadcrumb.Item key={key}>{content}</Breadcrumb.Item>;
  }

  render() {
    const {children, navigation} = this.props;
    const items = navigation.items;
    return (
      <div className="ant-layout-top">
        <div className="ant-layout-header">
          <div className="ant-layout-wrapper">
            <div className="ant-layout-logo"></div>
            <Menu theme="dark" mode="horizontal"
                  selectedKeys={[navigation.key]}
                  defaultSelectedKeys={[navigation.key]} style={{lineHeight: '64px'}}>
              <Menu.Item key="item"><a href={`${URL_PREFIX}/items`}> 监控项</a></Menu.Item>
              <Menu.Item key="template"><a href={`${URL_PREFIX}/templates`}>模板</a></Menu.Item>
              <Menu.Item key="app"><a href={`${URL_PREFIX}/apps`}>应用</a></Menu.Item>
            </Menu>
          </div>
        </div>
        <div className="ant-layout-wrapper">
          <div className="ant-layout-breadcrumb">
            <Breadcrumb>
              <Breadcrumb.Item><a href={URL_PREFIX}>首页</a></Breadcrumb.Item>
              {items.map((item, i) => this.renderBreadcrumbItem(item, i))}
            </Breadcrumb>
          </div>
          <div className="ant-layout-container">
            <div style={{ minHeight: 210 }}>
              {children}
            </div>
          </div>
        </div>
        <div className="ant-layout-footer">

        </div>
      </div>
    );
  }
}
