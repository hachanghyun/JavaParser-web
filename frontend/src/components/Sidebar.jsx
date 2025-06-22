import { useState } from 'react';
import AddProjectModal from './AddProjectModal';

function Sidebar({ projects, onAddProject, onSelectProject }) {
    const [showModal, setShowModal] = useState(false);

    return (
        <div style={{ width: '250px', backgroundColor: '#f1f1f1', padding: '16px' }}>
            <button
                onClick={() => setShowModal(true)}
                style={{
                    width: '100%',
                    padding: '10px',
                    backgroundColor: '#007bff',
                    color: 'white',
                    border: 'none',
                    borderRadius: '4px',
                    marginBottom: '16px',
                    cursor: 'pointer'
                }}
            >
                + 프로젝트 추가
            </button>

            <ul style={{ listStyle: 'none', padding: 0 }}>
                {projects.map((proj, idx) => (
                    <li
                        key={idx}
                        onClick={() => onSelectProject(proj)}
                        style={{ padding: '8px', cursor: 'pointer', borderBottom: '1px solid #ccc' }}
                    >
                        {proj.name}
                    </li>
                ))}
            </ul>

            {showModal && (
                <AddProjectModal
                    onClose={() => setShowModal(false)}
                    onSubmit={(project) => {
                        onAddProject(project);
                        setShowModal(false);
                    }}
                />
            )}
        </div>
    );
}

export default Sidebar;
