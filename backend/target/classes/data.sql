INSERT INTO clientes (nome, email, telefone, bairro) VALUES
('Ana Souza', 'ana.cliente@servicehub.com', '(91) 90000-0001', 'Nazaré'),
('Carlos Lima', 'carlos.cliente@servicehub.com', '(91) 90000-0002', 'Umarizal');

INSERT INTO diaristas (nome, email, telefone, bairro, experiencia, bio, especialidades, disponibilidade, materiais_proprios, agenda_flexivel, checklist, raio_atendimento_km, preco_base) VALUES
('Maria Silva', 'maria@servicehub.com', '(91) 98888-0000', 'Umarizal', '5 anos', 'Atendimento cuidadoso, foco em limpeza detalhada e organização do lar. Levo produtos e checklist personalizados.', 'Limpeza pesada,Organização,Pós-obra,Pet friendly', 'Alta', true, true, true, 8, 180.00),
('Joana Costa', 'joana@servicehub.com', '(91) 97777-0000', 'Batista Campos', '3 anos', 'Especialista em apartamentos e escritórios. Atendimento rápido e eficiente, com atenção aos detalhes.', 'Limpeza residencial,Escritório,Passadoria', 'Média', true, false, true, 5, 160.00);

INSERT INTO agendamentos (cliente_id, diarista_id, data_servico, status, observacoes) VALUES
(1, 1, '2026-03-05', 'CONFIRMADO', 'Apartamento 2 quartos'),
(2, 2, '2026-03-06', 'PENDENTE', 'Casa térrea');

INSERT INTO avaliacoes (cliente_id, diarista_id, nota, comentario) VALUES
(1, 1, 5, 'Excelente atendimento'),
(2, 2, 4, 'Muito bom, pontual');
