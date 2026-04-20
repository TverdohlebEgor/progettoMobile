package cohappy.backend.service;

import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.mappers.UserMapper;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.request.LoginDTO;
import cohappy.backend.model.dto.request.RegisterDTO;
import cohappy.backend.model.dto.response.UserAccountDTO;
import cohappy.backend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static cohappy.backend.util.StringCheckUtil.isEmptyString;

@AllArgsConstructor
@Service
public class ChoreService {


}
