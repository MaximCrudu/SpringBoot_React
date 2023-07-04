import React, { useEffect, useState } from 'react';
import { Image } from 'antd';
import { getInfoAboutProject } from './client';

const AboutProject = ({ infoPath }) => {
    const [projectDiagramUrl, setProjectDiagramUrl] = useState('');

    useEffect(() => {
        fetchData();
    });

    useEffect(() => {
    }, [projectDiagramUrl]);

    const fetchData = () => {
        getInfoAboutProject(infoPath)
            .then(response => {
                setProjectDiagramUrl(response);
            })
            .catch(error => {
                // Handle the error here
                console.error(error);
            });
    };

    return (
        <div>
            <br />
            <h1>
                You can find the source code and more information on my{' '}
                <a
                    rel="noopener noreferrer"
                    target="_blank"
                    href="https://github.com/MaximCrudu/SpringBoot_React/blob/main/README.md"
                >
                    GitHub Repository
                </a>
            </h1>
            <div>
                <br />
                <br />
                <h1>Project Diagram:</h1>
                <Image
                    src={projectDiagramUrl}
                    alt="Project Diagram"
                    style={{ width: '100%', maxWidth: '500px' }}
                />
            </div>
        </div>
    );
};

export default AboutProject;
