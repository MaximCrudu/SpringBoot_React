import { useState, useEffect } from "react";
import { getAllStudents, deleteStudent } from "./client";
import {
    Layout,
    Menu,
    Breadcrumb,
    Table,
    Spin,
    Empty,
    Button,
    Tag,
    Badge,
    Avatar,
    Popconfirm,
    Image,
    Divider
} from "antd";
import {
    DesktopOutlined,
    PieChartOutlined,
    FileOutlined,
    TeamOutlined,
    UserOutlined,
    LoadingOutlined,
    PlusOutlined,
    QuestionCircleOutlined,
    ExclamationCircleOutlined
} from "@ant-design/icons";

import StudentDrawerForm from "./StudentDrawerForm";

import "./App.css";
import { errorNotification, successNotification } from "./Notification";

const { Header, Content, Footer, Sider } = Layout;
const { SubMenu } = Menu;

const getRandomHue = (name) => {
    // The nameHash will contain the sum of the character codes of all the characters in the name string
    const nameHash = name.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
    return nameHash % 360; // Generate a random hue value based on the name
};

const TheAvatar = ({ name }) => {
    let trim = name.trim();
    if (trim.length === 0) {
        return <Avatar icon={<UserOutlined />} />;
    }
    const split = trim.split(" ");
    const randomHue = getRandomHue(name);

    return (
        <Avatar
            className="random-avatar-color"
            style={{ backgroundColor: `hsl(${randomHue}, 80%, 80%)` }}
        >
            {split.length === 1 ? name.charAt(0) : `${name.charAt(0)}${name.charAt(name.length - 1)}`}
        </Avatar>
    );
};

const ActionButtons = (student, callback, showDrawer, setShowDrawer, setSelectedStudent) => {
    const removeStudent = (student, callback) => {
        deleteStudent(student.id).then(() => {
            successNotification(
                "Student deleted",
                `Student ${student.name} was deleted`
            );
            callback();
        }).catch(err => {
            err.response.json().then(res => {
                errorNotification(
                    "There was an issue",
                    `${res.message} [${res.status}] [${res.error}]`
                );
            });
        });
    };
    const editStudent = (student, setSelectedStudent) => {
        setSelectedStudent(student);
        setShowDrawer(!showDrawer);
    };

    return (
        <>
            <Popconfirm
                style={{ color: "red" }}
                placement="topRight"
                title={`Are you sure to delete ${student.name} from the list?`}
                icon={<ExclamationCircleOutlined style={{ color: 'red' }} />}
                description="Confirm to delete a student from the list"
                onConfirm={() => removeStudent(student, callback)}
                okText="Yes"
                cancelText="No"
            >
                <Button value="default">Delete</Button>
            </Popconfirm>
            <Popconfirm
                placement="topRight"
                icon={<QuestionCircleOutlined />}
                title={`Are you sure to edit student ${student.name} ?`}
                description="Confirm to edit a student"
                onConfirm={() => editStudent(student, setSelectedStudent)}
                okText="Yes"
                cancelText="No"
            >
                <Button value="default">Edit</Button>
            </Popconfirm>
        </>
    );
};

const columns = (fetchStudents, showDrawer, setShowDrawer, setSelectedStudent) => [
    {
        title: '',
        dataIndex: 'avatar',
        key: 'avatar',
        render: (text, student) => <TheAvatar name={student.name} />
    },
    {
        title: 'Name',
        dataIndex: 'name',
        key: 'name'
    },
    {
        title: 'Email',
        dataIndex: 'email',
        key: 'email'
    },
    {
        title: 'Gender',
        dataIndex: 'gender',
        key: 'gender'
    },
    {
        title: 'Actions',
        dataIndex: 'actions',
        key: 'actions',
        render: (text, student) =>
            ActionButtons(student, fetchStudents, showDrawer, setShowDrawer, setSelectedStudent)
    }
];

const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;

function App() {
    const [students, setStudents] = useState([]);
    const [collapsed, setCollapsed] = useState(false);
    const [fetching, setFetching] = useState(true);
    const [showDrawer, setShowDrawer] = useState(false);
    const [selectedStudent, setSelectedStudent] = useState(null);

    const fetchStudents = () =>
        getAllStudents()
            .then(res => res.json())
            .then(data => {
                setStudents(data);
            }).catch(err => {
            err.response.json().then(res => {
                errorNotification(
                    "There was an issue",
                    `${res.message} [StatusCode:${res.status}] [${res.error}]`
                );
            });
        }).finally(() => setFetching(false));

    useEffect(() => {
        fetchStudents();
    }, []); // zero dependencies

    const renderStudents = () => {
        const columnsConfig = columns(fetchStudents, showDrawer, setShowDrawer, setSelectedStudent);
        if (fetching) {
            return <Spin indicator={antIcon} />;
        }
        if (students.length <= 0) {
            return (
                <>
                    <Button
                        onClick={() => {
                            setSelectedStudent(null);
                            setShowDrawer(!showDrawer);
                        }}
                        type="primary" shape="round" icon={<PlusOutlined />} size="small"
                    >
                        Add New Student
                    </Button>
                    <StudentDrawerForm
                        showDrawer={showDrawer}
                        setShowDrawer={setShowDrawer}
                        fetchStudents={fetchStudents}
                        selectedStudent={selectedStudent}
                    />
                    <Empty />
                </>
            );
        }
        return (
            <>
                <StudentDrawerForm
                    showDrawer={showDrawer}
                    setShowDrawer={setShowDrawer}
                    fetchStudents={fetchStudents}
                    selectedStudent={selectedStudent}
                />
                <Table
                    dataSource={students}
                    columns={columnsConfig}
                    bordered
                    title={() => (
                        <>
                            <Tag>Number of students</Tag>
                            <Badge count={students.length} className="site-badge-count-4" />
                            <br /><br />
                            <Button
                                onClick={() => {
                                    setSelectedStudent(null);
                                    setShowDrawer(!showDrawer);
                                }}
                                type="primary" shape="round" icon={<PlusOutlined />} size="small"
                            >
                                Add New Student
                            </Button>
                        </>
                    )}
                    pagination={{ pageSize: 50 }}
                    scroll={{ y: 550 }}
                    rowKey={student => student.id}
                />
            </>
        );
    };

    return (
        <Layout style={{ minHeight: '100vh' }}>
            <Sider collapsible collapsed={collapsed} onCollapse={setCollapsed}>
                <div className="logo" />
                <Menu theme="dark" defaultSelectedKeys={['1']} mode="inline">
                    <Menu.Item key="1" icon={<PieChartOutlined />}>
                        Option 1
                    </Menu.Item>
                    <Menu.Item key="2" icon={<DesktopOutlined />}>
                        Option 2
                    </Menu.Item>
                    <SubMenu key="sub1" icon={<UserOutlined />} title="User">
                        <Menu.Item key="3">Tom</Menu.Item>
                        <Menu.Item key="4">Bill</Menu.Item>
                        <Menu.Item key="5">Alex</Menu.Item>
                    </SubMenu>
                    <SubMenu key="sub2" icon={<TeamOutlined />} title="Team">
                        <Menu.Item key="6">Team 1</Menu.Item>
                        <Menu.Item key="8">Team 2</Menu.Item>
                    </SubMenu>
                    <Menu.Item key="9" icon={<FileOutlined />}>
                        Files
                    </Menu.Item>
                </Menu>
            </Sider>
            <Layout className="site-layout">
                <Header className="site-layout-background" style={{ padding: 0 }} />
                <Content style={{ margin: '0 16px' }}>
                    <Breadcrumb style={{ margin: '16px 0' }}>
                        <Breadcrumb.Item>User</Breadcrumb.Item>
                        <Breadcrumb.Item>Bill</Breadcrumb.Item>
                    </Breadcrumb>
                    <div className="site-layout-background" style={{ padding: 24, minHeight: 360 }}>
                        {renderStudents()}
                    </div>
                </Content>
                <Footer style={{ textAlign: 'center' }}>
                    <Image
                        width={55}
                        src="https://upload.wikimedia.org/wikipedia/commons/c/ca/LinkedIn_logo_initials.png"
                    />
                    <Divider>
                        <a
                            rel="noopener noreferrer"
                            target="_blank"
                            href="https://www.linkedin.com/in/maxim-crudu/"
                        >
                            My LinkedIn Account
                        </a>
                    </Divider>
                </Footer>
            </Layout>
        </Layout>
    );
}

export default App;
