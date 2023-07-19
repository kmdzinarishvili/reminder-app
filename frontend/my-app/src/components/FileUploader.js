
import axios from 'axios';
 
 
export default function FileUploader({setAttachment}) {
    const onFileChange = event => {
        setAttachment(event.target.files[0]);
    };
 
        return (
            <div>
                <h4>Attachment</h4>
                <div>
                    <input type="file" onChange={onFileChange} />
                </div>
            </div>
        );
    }
 