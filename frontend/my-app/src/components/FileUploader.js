
import axios from 'axios';
 
import React, { useState } from 'react';
 
export default function FileUploader({setAttachment}) {

 
    const onFileChange = event => {
        console.log(event);
        console.log(event.target.files[0])
        setAttachment(event.target.files[0]);
    };
 
    // On file upload (click the upload button)
    const onFileUpload = () => {

        // Create an object of formData
        // const formData = new FormData();
 
        // // Update the formData object
        // formData.append(
        //     "myFile",
        //     this.state.selectedFile,
        //     this.state.selectedFile.name
        // );
 
        // // Details of the uploaded file
        // console.log(this.state.selectedFile);
 
        // // Request made to the backend api
        // // Send formData object
        // axios.post("api/uploadfile", formData);
    };
 
    // File content to be displayed after
    // file upload is complete
 
        return (
            <div>
                <h4>Attachment</h4>
                <div>
                    <input type="file" onChange={onFileChange} />
                </div>
            </div>
        );
    }
 