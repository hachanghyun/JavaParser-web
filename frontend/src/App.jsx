import { useState } from 'react';
import Sidebar from './components/Sidebar';
import MainView from './components/MainView';
import './index.css';

function App() {
    const [projects, setProjects] = useState([]);
    const [selectedProject, setSelectedProject] = useState(null);

    const addProject = (project) => {
        setProjects([...projects, project]);
        setSelectedProject(project);
    };

    return (
        <div style={{ display: 'flex', height: '100vh' }}>
            <Sidebar
                projects={projects}
                onAddProject={addProject}
                onSelectProject={setSelectedProject}
            />
            <MainView project={selectedProject} />
        </div>
    );
}

export default App;
