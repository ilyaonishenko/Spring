package com.redactus.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.redactus.dao.FileMetaDao;
import com.redactus.dao.SequenceDao;
import com.redactus.model.FileMeta;
import java.util.List;
@Service
public class FileMetaService{
	@Autowired private FileMetaDao fileMetaDao;
	@Autowired private SequenceDao sequenceDao;
	public void add(FileMeta fileMeta) {
		fileMeta.setId(sequenceDao.getNextSequenceId(FileMeta.COLLECTION_NAME));
		fileMetaDao.save(fileMeta);
	}
	public void update(FileMeta fileMeta) {
        fileMetaDao.save(fileMeta);
    }

    public FileMeta get(Long id) {
        return fileMetaDao.get(id);
    }

    public List<FileMeta> getAll() {
        return fileMetaDao.getAll();
    }

    public void remove(Long id) {
        fileMetaDao.remove(id);
    }
}
