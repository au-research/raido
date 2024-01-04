import React from 'react';
import copy from 'clipboard-copy';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';

interface CopyButtonProps {
    textToCopy: string;
}

const CopyButton: React.FC<CopyButtonProps> = ({ textToCopy }) => {
    const handleCopyClick = async () => {
        try {
            await copy(textToCopy);
        } catch (error) {
            console.log("Error copying to clipboard");
        }
    };

    return (
        <button onClick={handleCopyClick}>
            <ContentCopyIcon color="disabled"/>
        </button>
    );
};

export default CopyButton;
