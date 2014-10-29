package org.generationcp.ibpworkbench.controller;

import org.apache.commons.io.IOUtils;
import org.generationcp.ibpworkbench.service.BackupIBDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * GCP
 */

@Controller
@RequestMapping("/backupIBDB")
public class BackupIBDBController {
    @Autowired
    private BackupIBDBService backupIBDBService;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody void backupIBDB(@RequestParam("projectId") String projectId, @RequestParam("localDbName") String localDbName,
                                         HttpServletResponse resp)
        throws Exception {
        if(projectId == null) {
            throw new Exception("Error: Project ID is missing!");
        }
        FileInputStream inputStream = null;
        try {
            File backupFile = backupIBDBService.backupIBDB(projectId, localDbName);
            inputStream = new FileInputStream(backupFile);

            String allBackup = IOUtils.toString(inputStream);
            OutputStream out = resp.getOutputStream();
            resp.setContentType("text/plain; charset=utf-8");
            
            
            // only add .sql extension if backupFile.getName() does not already have this
            if (!getExtension(backupFile).toLowerCase().contains("sql")) {
            	resp.addHeader("Content-Disposition","attachment; filename=\"" + backupFile.getName() + ".sql\"");
            } else {
                resp.addHeader("Content-Disposition", "attachment; filename=\"" + backupFile.getName() + "\"");
            }
            
            out.write(allBackup.getBytes());
            out.flush();
            out.close();
        } finally {
            inputStream.close();
        }
    }
    
	/**
	 * Get filename Extension
	 * @param f
	 * @return
	 */
	private static String getExtension(File f)
	{
	String ext = null;
	String s = f.getName();
	int i = s.lastIndexOf('.');

	if (i > 0 && i < s.length() - 1) {
        ext = s.substring(i + 1).toLowerCase();
    }

	if(ext == null) {
        return "";
    }
	return ext;
	}

    
}
