import {
    Drawer,
    Input,
    Col,
    Select,
    Form,
    Row,
    Button,
    Spin
} from "antd";
import {addNewStudent, updateStudent} from "./client";
import {LoadingOutlined} from "@ant-design/icons";
import {useEffect, useState} from "react";
import {successNotification, errorNotification} from "./Notification";
import "./StudentDrawerForm.css";

const {Option} = Select;

const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;

function StudentDrawerForm({showDrawer, setShowDrawer, fetchStudents, selectedStudent}) {

    const [form] = Form.useForm();
    const [disableUpdate, setDisableUpdate] = useState(true);

    useEffect(() => {
        form.setFieldsValue(selectedStudent);
    }, [selectedStudent, form]);

    useEffect(() => {
        form.resetFields();
    }, [form, showDrawer]);

    const onClose = () => {
        setShowDrawer(false);
        setDisableUpdate(true);
    };

    const[submitting, setSubmitting] = useState(false);

    function updateSelectedStudent(id, student) {
        setSubmitting(true);
        updateStudent(id, student)
            .then(() => {
                onClose();
                successNotification(
                    "Student edited",
                    `Student ${student.name} with Id: ${id} was edited`
                );
                fetchStudents();
            }).catch(err => {
            err.response.json().then(res => {
                errorNotification(
                    "There was an issue",
                    `${res.message} [${res.status}] [${res.error}]`
                );
            });
        }).finally(() => {
            setSubmitting(false);
        })
    }

    function addStudent(student) {
        setSubmitting(true);
        addNewStudent(student)
            .then(() => {
                onClose();
                successNotification(
                    "Student successfully added",
                    `${student.name} was added to the system`
                )
                fetchStudents(); // fetch all students to refresh the table after adding a new one
            }).catch(err => {
            err.response.json().then(res => {
                errorNotification(
                    "There was an issue",
                    `${res.message} [${res.status}] [${res.error}]`,
                    "bottomLeft"
                )
            });
        }).finally(() => {
            setSubmitting(false);
        })
    }

    const onFinish = student => {
        if (selectedStudent) {
            updateSelectedStudent(selectedStudent.id, student);
        } else {
            addStudent(student);
        }
    };

    const onFinishFailed = errorInfo => {
        alert(JSON.stringify(errorInfo, null, 2));
    };

    // Set initial form values if a student is selected
    const initialValues = selectedStudent
        ? {
            name: selectedStudent.name,
            email: selectedStudent.email,
            gender: selectedStudent.gender,
        }
        : {};

    function submitForm() {
        function resetDataFields() {
            form.resetFields();
            setDisableUpdate(true);
        }
        if (selectedStudent) {
            return (
                <>
                    <Button type="primary" htmlType="submit" disabled={disableUpdate} style={{ marginRight: 8 }}>
                        {"Update"}
                    </Button>
                    <Button
                        className="update-button"
                        type="primary"
                        htmlType="reset"
                        hidden={disableUpdate}
                        onClick={resetDataFields}
                    >
                        {"Reset"}
                    </Button>
                </>
            );

        }
        return (
            <Button type="primary" htmlType="submit">
                {"Submit"}
            </Button> );
    }

    const onFormValuesChange = (changedValues, allValues) => {
        if (selectedStudent) {
            const updatedData = allValues;
            const isSame = Object.keys(selectedStudent).every(key => {
                if (key === 'id') {
                    return true; // Skip the 'id' property
                }
                return selectedStudent[key] === updatedData[key];
            });
            return setDisableUpdate(isSame);
        }
    };

    return <Drawer
        getContainer={false}
        title={selectedStudent ? "Edit student" : "Create new student"}
        width={720}
        onClose={onClose}
        visible={showDrawer}
        bodyStyle={{paddingBottom: 80}}
        footer={
            <div
                style={{
                    textAlign: 'right',
                }}
            >
                <Button onClick={onClose} style={{marginRight: 8}}>
                    Cancel
                </Button>
            </div>
        }
    >
        <Form layout="vertical"
              form={form} // Provide the form object obtained from useForm
              onFinishFailed={onFinishFailed}
              onFinish={onFinish}
              initialValues={initialValues} // Set the initial form values
              onValuesChange={onFormValuesChange}
        >
            <Row gutter={16}>
                <Col span={12}>
                    <Form.Item
                        name="name"
                        label="Name"
                        rules={[{required: true, message: 'Please enter student name'}]}
                    >
                        <Input placeholder="Please enter student name"/>
                    </Form.Item>
                </Col>
                <Col span={12}>
                    <Form.Item
                        name="email"
                        label="Email"
                        rules={[{required: true, message: 'Please enter student email'}]}
                    >
                        <Input placeholder="Please enter student email"/>
                    </Form.Item>
                </Col>
            </Row>
            <Row gutter={16}>
                <Col span={12}>
                    <Form.Item
                        name="gender"
                        label="Gender"
                        rules={[{required: true, message: 'Please select a gender'}]}
                    >
                        <Select placeholder="Please select a gender">
                            <Option value="MALE">MALE</Option>
                            <Option value="FEMALE">FEMALE</Option>
                            <Option value="OTHER">OTHER</Option>
                        </Select>
                    </Form.Item>
                </Col>
            </Row>
            <Row>
                <Col span={12}>
                    <Form.Item >
                        {submitForm()}
                    </Form.Item>
                </Col>
            </Row>
            <Row>
                {submitting && <Spin indicator={antIcon} />}
            </Row>
        </Form>
    </Drawer>
}

export default StudentDrawerForm;