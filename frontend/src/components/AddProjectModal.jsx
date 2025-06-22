import { useState } from 'react';
import axios from 'axios';

function AddProjectModal({ onClose, onSubmit }) {
    const [name, setName] = useState('');
    const [path, setPath] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async () => {
        if (!name || !path) {
            alert('이름과 경로를 모두 입력해주세요.');
            return;
        }

        setLoading(true);
        try {
            const res = await axios.post('http://localhost:8080/api/project', { name, path });
            const projectId = res.data.projectId;

            const newProject = { name, path, projectId };
            onSubmit(newProject);
            onClose();
        } catch (err) {
            console.error('❌ 프로젝트 등록 실패:', err);
            alert('프로젝트 등록에 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{
            position: 'fixed',
            top: 0, left: 0, right: 0, bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.5)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            zIndex: 1000
        }}>
            <div style={{ backgroundColor: 'white', padding: '24px', borderRadius: '8px', width: '400px' }}>
                <h2 style={{ marginBottom: '16px' }}>프로젝트 추가</h2>

                <input
                    type="text"
                    placeholder="프로젝트 이름"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    style={{ width: '100%', padding: '8px', marginBottom: '12px' }}
                    disabled={loading}
                />

                <input
                    type="text"
                    placeholder="예: /Users/abc/workspace/myproject"
                    value={path}
                    onChange={(e) => setPath(e.target.value)}
                    style={{ width: '100%', padding: '8px', marginBottom: '12px' }}
                    disabled={loading}
                />

                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px' }}>
                    <button onClick={onClose} style={{ padding: '8px 12px' }} disabled={loading}>취소</button>
                    <button
                        onClick={handleSubmit}
                        style={{ padding: '8px 12px', backgroundColor: '#007bff', color: 'white', border: 'none' }}
                        disabled={loading}
                    >
                        {loading ? '등록 중...' : '추가'}
                    </button>
                </div>
            </div>
        </div>
    );
}

export default AddProjectModal;
